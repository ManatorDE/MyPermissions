package de.manator.mypermissions.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;

public class WebServer {

    public static void main(String[] args) {
        WebServer server = new WebServer(null);
        server.startServer();
    }

    private Thread serverThread;
    private boolean running;
    private Main main;

    public WebServer(Main main) {
        running = false;
        this.main = main;
        serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                int port = 8080;
                if(main != null) port = main.getConfigFile().getWebserverPort();
                try (ServerSocket serverSocket = new ServerSocket(port)) {
                    if(main != null) main.getLogger().info("WebServer started at port " + port);
                    while (running) {
                        try (Socket socket = serverSocket.accept()) {
                            handleClient(socket);
                        } catch(IOException e) {
                            if(main != null) {
                                main.getLogger().info("Socket connection crashed!");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isRunning() {
        return running;
    }

    public void startServer() {
        running = true;
        serverThread.start();
    }

    public void stopServer() {
        running = false;
        serverThread.interrupt();
    }

    private void handleClient(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);

        // Read the request
        ArrayList<String> lines = new ArrayList<String>();
        boolean isPost = false;
        int contentLength = 0;
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
            if(line.startsWith("POST")) {
                isPost = true;
            }
            if(line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split("\s+")[1]);
            }
        }

        if(!lines.isEmpty() && isPost) {
            char[] body = new char[contentLength];
            in.read(body, 0, contentLength);
            StringBuilder sb = new StringBuilder();
            sb.append(body);
            doPost(lines, writer, sb.toString());
        } else if(!lines.isEmpty()) {
            doGet(lines, writer);
        }

        writer.flush();
    }

    private void doPost(ArrayList<String> request, PrintWriter writer, String body) throws IOException {
        String path = request.get(0).split("\s+")[1];

        String httpResponse = "";
        boolean cookies = false;

        HashMap<String, String> postMap = new HashMap<String, String>();

        for(String c : body.split("&")) {
            String[] arg = c.split("=");
            if(arg.length == 2) {
                postMap.put(arg[0], arg[1]);
            }
        }

        handlePost(postMap);

        if(postMap.containsKey("cookies")) {
            httpResponse += setCookieString("cookies", postMap.get("cookies"));
            cookies = true;
        }

        if(postMap.containsKey("user") &&  postMap.containsKey("password")) {
            String user = postMap.get("user");
            String password = postMap.get("password");
            if(main != null && user.equals(main.getConfigFile().getUsername()) && password.equals(main.getConfigFile().getPassword())) {
                httpResponse += setCookieString("logged-in", "1", 600);
            } else if(main == null) {
                httpResponse += setCookieString("logged-in", "1", 600);
            }
            cookies = true;
        }

        if(cookies) {
            httpResponse += "Location: " + path;
        }
        // Handle get request
        doGet(request, writer, httpResponse, cookies);
    }

    private void doGet(ArrayList<String> request, PrintWriter writer) throws IOException {
        doGet(request, writer, null, false);
    }

    private void doGet(ArrayList<String> request, PrintWriter writer, String resp, boolean redirect) throws IOException {
        if(request.isEmpty()) {
            String httpResponse = "HTTP/1.1 404 OK\r\n\r\n";
            writer.write(httpResponse);
            return;
        }
        String path = "";
        String[] req = request.get(0).split("\s+"); 
        if(req.length >= 2) {
            path = req[1];
        }
        String cookie = null;
        boolean cookies = false;
        for(String line : request) {
            if(line.startsWith("Cookie")) {
                cookie = line;
                cookies = true;
                break;
            }
        }

        if(cookie != null && !cookie.contains("logged-in")) cookie = null;

        String httpResponse = "";

        httpResponse = "HTTP/1.1 200 OK\r\n";
        
        if(redirect || path.isEmpty()) {
            httpResponse = "HTTP/1.1 303 See Other\r\n";
        }

        if(path.startsWith("/?action=logout")) {
            httpResponse = "HTTP/1.1 303 See Other\r\n";
            httpResponse += setCookieString("logged-in", "1", 0);
            httpResponse += "Location: /\r\n";
            writer.write(httpResponse);
            return;
        } else if(path.equals("/reload")) {
            main.reloadPlayers();
            httpResponse = "HTTP/1.1 303 See Other\r\n";
            httpResponse += "Location: /\r\n";
            writer.write(httpResponse);
            return;
        } else if(path.isEmpty()) {
            httpResponse += "Location: /\r\n";
            writer.write(httpResponse);
        }

        if(resp != null) {
            httpResponse += resp + "\r\n";
        } else {
            httpResponse += "\r\n";
        }

        // Handle get request
        if(path.equals("/")) {
            String title = "MyPermissions Panel";
            httpResponse += Templates.startHtml();
            httpResponse += Templates.getHead(title);
            if(cookie == null) {
                httpResponse += Templates.getLoginBody(title, cookies);
            } else {
                httpResponse += Templates.getBody(title, path, main);
            }
            httpResponse += Templates.endHtml();
        } else if(path.startsWith("/groups")) {
            String title = "MyPermissions Panel - Groups";
            httpResponse += Templates.startHtml();
            httpResponse += Templates.getHead(title);
            if(cookie == null) {
                httpResponse += Templates.getLoginBody(title, cookies);
            } else {
                httpResponse += Templates.getBody(title, path, main);
            }
            httpResponse += Templates.endHtml();
        } else if(path.startsWith("/permissions")) {
            String title = "MyPermissions Panel - Permissions";
            httpResponse += Templates.startHtml();
            httpResponse += Templates.getHead(title);
            if(cookie == null) {
                httpResponse += Templates.getLoginBody(title, cookies);
            } else {
                httpResponse += Templates.getBody(title, path, main);
            }
            httpResponse += Templates.endHtml();
        } else if(path.equals("/style/style.css")) {
            httpResponse += Templates.getStyles();
        } else {
            httpResponse = "HTTP/1.1 404 OK\r\n\r\n";
        }

        writer.write(httpResponse);
    }

    private void handlePost(HashMap<String, String> postMap) {
        if(main != null) {
            if(postMap.containsKey("action") && postMap.containsKey("group")) {
                if(postMap.get("action").equals("config")) {
                    GroupHandler gh = main.getGroupHandler();
                    Group g = gh.getGroup(postMap.get("group"));
                    if(postMap.containsKey("op"))
                        gh.setOp(g, postMap.get("op").equals("on"));
                    if(postMap.containsKey("default") && postMap.get("default").equals("on")) {
                        gh.setDefault(g);
                    } else if(gh.getDefault() != null && gh.getDefault().equals(g)) {
                        gh.removeDefault();
                    }
                    if(postMap.containsKey("prefix")) {
                        String prefix;
                        try {
                            prefix = URLDecoder.decode(postMap.get("prefix"), StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            prefix = postMap.get("prefix");
                        }
                        gh.setPrefix(g, prefix);
                    }
                    if(postMap.containsKey("suffix")) {
                        String suffix;
                        try {
                            suffix = URLDecoder.decode(postMap.get("suffix"), StandardCharsets.UTF_8.name());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            suffix = postMap.get("suffix");
                        }
                        gh.setSuffix(g, suffix);
                    }
                    if(postMap.containsKey("rank"))
                        gh.setRank(g, Integer.parseInt(postMap.get("rank")));
                } else if(postMap.get("action").equals("perms")) {
                    if(postMap.containsKey("permissions")) {
                        String[] perms = postMap.get("permissions").split("%0D%0A");
                        GroupHandler gh = main.getGroupHandler();
                        Group g = gh.getGroup(postMap.get("group")); 
                        gh.getPermissions(g);
                        LinkedList<String> l = new LinkedList<>();
                        for(String s : perms) {
                            l.add(s);
                        }
                        gh.setPermissions(l, g);
                    }
                } else if(postMap.get("action").equals("playeradd") && postMap.containsKey("player")) {
                    GroupHandler gh = main.getGroupHandler();
                    PlayerHandler ph = main.getPlayerHandler();
                    ph.addGroup(gh.getGroup(postMap.get("group")), postMap.get("player"));
                } else if(postMap.get("action").equals("playerrm")  && postMap.containsKey("player")) {
                    GroupHandler gh = main.getGroupHandler();
                    PlayerHandler ph = main.getPlayerHandler();
                    ph.removeGroup(gh.getGroup(postMap.get("group")), postMap.get("player"));

                } else if(postMap.get("action").equals("nperms")) {
                    if(postMap.containsKey("npermissions")) {
                        if(postMap.containsKey("npermissions")) {
                            String[] perms = postMap.get("npermissions").split("%0D%0A");
                            GroupHandler gh = main.getGroupHandler();
                            Group g = gh.getGroup(postMap.get("group")); 
                            gh.getNegatedPermissions(g);
                            LinkedList<String> l = new LinkedList<>();
                            for(String s : perms) {
                                l.add(s);
                            }
                            gh.setNegatedPermissions(l, g);
                        }
                    }
                } else if(postMap.get("action").equals("deletegroup")) {
                    main.getGroupHandler().deleteGroup(main.getGroupHandler().getGroup(postMap.get("group")));
                }
            } else if(postMap.containsKey("action") && postMap.containsKey("player")) {
                if(postMap.get("action").equals("permsplayer")) {
                    if(postMap.containsKey("permissions")) {
                        String[] perms = postMap.get("permissions").split("%0D%0A");
                        PlayerHandler ph = main.getPlayerHandler();
                        ph.getPermissions(postMap.get("player"));
                        LinkedList<String> l = new LinkedList<>();
                        for(String s : perms) {
                            l.add(s);
                        }
                        ph.setPermissions(l, postMap.get("player"));
                    } else {
                        PlayerHandler ph = main.getPlayerHandler();
                        ph.getPermissions(postMap.get("player"));
                        LinkedList<String> l = new LinkedList<>();
                        ph.setPermissions(l, postMap.get("player"));
                    }
                } else if(postMap.get("action").equals("npermsplayer")) {
                    if(postMap.containsKey("npermissions")) {
                        String[] perms = postMap.get("npermissions").split("%0D%0A");
                        PlayerHandler ph = main.getPlayerHandler();
                        ph.getNegatedPermissions(postMap.get("player"));
                        LinkedList<String> l = new LinkedList<>();
                        for(String s : perms) {
                            l.add(s);
                        }
                        ph.setNegatedPermissions(l, postMap.get("player"));
                    } else {
                        PlayerHandler ph = main.getPlayerHandler();
                        ph.getNegatedPermissions(postMap.get("player"));
                        LinkedList<String> l = new LinkedList<>();
                        ph.setNegatedPermissions(l, postMap.get("player"));
                    }
                }
            } else if(postMap.containsKey("action")) {
                if(postMap.get("action").equals("creategroup") && postMap.containsKey("name") && postMap.containsKey("super")) {
                    if(postMap.get("super").equals("nogroup")) {
                        main.getGroupHandler().addGroup(postMap.get("name"));
                    } else {
                        main.getGroupHandler().addGroup(postMap.get("name"), main.getGroupHandler().getGroup(postMap.get("super")));
                    }
                }
            }
            main.getGroupHandler().loadGroups();
            main.reloadPlayers();
        }
    }

    private String setCookieString(String key, String value) {
        return  "Set-Cookie: " + key + "=" + value + "\r\n";
    }

    private String setCookieString(String key, String value, int time) {
        return  "Set-Cookie: " + key + "=" + value + "; Max-Age=" + time + "\r\n";
    }
}
