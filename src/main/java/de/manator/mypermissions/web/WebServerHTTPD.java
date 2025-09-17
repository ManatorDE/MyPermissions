package de.manator.mypermissions.web;

import de.manator.mypermissions.Main;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class WebServerHTTPD extends NanoHTTPD {

    private final Main main;

    public WebServerHTTPD(Main main, int port) {
        super(port);
        this.main = main;
        try {
            start(SOCKET_READ_TIMEOUT, false);
            main.getLogger().info("Web interface started on port " + port);
        } catch (Exception e) {
            main.getLogger().severe("Could not start web interface on port " + port + ": " + e.getMessage());
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        main.getLogger().info("Received request: " + session.getMethod() + " " + uri);
        if(session.getMethod() == Method.GET) {
            if (uri.equals("/") || uri.isEmpty()) {
                return newFixedLengthResponse(PageBuilder.getIndexPage(main));
            } else if (uri.equals("/groups/") || uri.equals("/groups")) {
                return newFixedLengthResponse(PageBuilder.getGroupsPage(main));
            } else if (uri.equals("/players/") || uri.equals("/players")) {
                return newFixedLengthResponse(PageBuilder.getPlayersPage(main));
            } else if (uri.startsWith("/players/") && uri.length() > 9) {
                String playerName = uri.split("/")[2];
                main.getLogger().info(playerName);
                return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
            } else if (uri.startsWith("/groups/") && uri.length() > 8) {
                String groupName = uri.split("/")[2];
                return newFixedLengthResponse(PageBuilder.getGroupPage(main, groupName));
            } else if(uri.equals("/login/") || uri.equals("/login")) {
                return  newFixedLengthResponse(PageBuilder.getLoginPage());
            } else if (uri.equals("/logo.png")) {
                InputStream logoStream = PageBuilder.getLogoInputStream();
                if (logoStream == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    return newFixedLengthResponse(Response.Status.OK, "image/png", logoStream, logoStream.available());
                } catch (IOException e) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
            } else if (uri.equals("/manage") || uri.equals("/manage/")) {
                return newFixedLengthResponse(PageBuilder.getManagePlguinPage(main));
            } else if (uri.equals("/create-group") || uri.equals("/create-group/")) {
                return newFixedLengthResponse(PageBuilder.getCreateGroupPage(main));

            }
        } else if(session.getMethod() == Method.POST) {
            if(uri.equals("/login") || uri.equals("/login/")) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if(cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getIndexPage(main));
                }
                if(handleLogin(session)) {
                    Response response = newFixedLengthResponse(PageBuilder.getIndexPage(main));
                    response.addHeader("Set-Cookie", "loggedin=true; Max-Age=1800;");

                    return response;
                } else {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
            }
        }
        return newFixedLengthResponse(PageBuilder.getNotFoundPage());
    }

    private boolean handleLogin(IHTTPSession session) {
        HashMap<String, String> postData = new HashMap<>();
        try {
            session.parseBody(postData);
            String username = session.getParameters().getOrDefault("username", null).getFirst();
            String password = session.getParameters().getOrDefault("password", null).getFirst();
            return main.getConfigFile().getUsername().equals(username) && main.getConfigFile().getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

}
