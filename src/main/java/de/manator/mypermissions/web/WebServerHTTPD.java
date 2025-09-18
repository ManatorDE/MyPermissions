package de.manator.mypermissions.web;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
        if(session.getMethod() == Method.GET) {
            if (uri.equals("/") || uri.isEmpty()) {
                return newFixedLengthResponse(PageBuilder.getIndexPage(main));
            } else if (uri.equals("/groups/") || uri.equals("/groups")) {
                return newFixedLengthResponse(PageBuilder.getGroupsPage(main));
            } else if (uri.equals("/players/") || uri.equals("/players")) {
                return newFixedLengthResponse(PageBuilder.getPlayersPage(main));
            } else if (uri.startsWith("/players/") && uri.length() > 9) {
                String playerName = uri.split("/")[2];
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
            switch (uri) {
                case "/login", "/login/" -> {
                    HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                    String cookie = headers.getOrDefault("cookie", "");
                    if (cookie.contains("loggedin=true")) {
                        return newFixedLengthResponse(PageBuilder.getIndexPage(main));
                    }
                    if (handleLogin(session)) {
                        Response response = newFixedLengthResponse(PageBuilder.getIndexPage(main));
                        response.addHeader("Set-Cookie", "loggedin=true; Max-Age=1800;");

                        return response;
                    } else {
                        return newFixedLengthResponse(PageBuilder.getLoginPage());
                    }
                }
                case "/create-group", "/create-group/" -> {
                    HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                    String cookie = headers.getOrDefault("cookie", "");
                    if (!cookie.contains("loggedin=true")) {
                        return newFixedLengthResponse(PageBuilder.getLoginPage());
                    }
                    HashMap<String, String> postData = new HashMap<>();
                    try {
                        session.parseBody(postData);
                        String groupName = session.getParameters().getOrDefault("new-group-name", null).getFirst();
                        String superGroup = session.getParameters().getOrDefault("new-group-super", null).getFirst();
                        if (groupName == null || groupName.isEmpty()) {
                            return newFixedLengthResponse(PageBuilder.getCreateGroupPage(main));
                        }
                        if (main.getGroupHandler().getGroup(groupName) != null) {
                            return newFixedLengthResponse(PageBuilder.getCreateGroupPage(main));
                        }
                        if (superGroup != null && !superGroup.isEmpty() && main.getGroupHandler().getGroup(superGroup) == null) {
                            return newFixedLengthResponse(PageBuilder.getCreateGroupPage(main));
                        }
                        if (superGroup != null && !superGroup.isEmpty()) {
                            main.getGroupHandler().addGroup(groupName, main.getGroupHandler().getGroup(superGroup));
                        } else {
                            main.getGroupHandler().addGroup(groupName);
                        }
                        return newFixedLengthResponse(PageBuilder.getGroupPage(main, groupName));
                    } catch (Exception e) {
                        return newFixedLengthResponse(PageBuilder.getCreateGroupPage(main));
                    }
                }
                case "/settings/reload", "/settings/reload/" -> {
                    main.getGroupHandler().loadGroups();
                    main.reloadPlayers();
                    return newFixedLengthResponse(PageBuilder.getManagePlguinPage(main));
                }
                case "/settings", "/settings/" -> {
                    HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                    String cookie = headers.getOrDefault("cookie", "");
                    if (!cookie.contains("loggedin=true")) {
                        return newFixedLengthResponse(PageBuilder.getLoginPage());
                    }
                    HashMap<String, String> postData = new HashMap<>();
                    try {
                        session.parseBody(postData);
                        String prefixSpace = session.getParameters().getOrDefault("prefix-space", List.of("off")).getFirst();
                        String suffixSpace = session.getParameters().getOrDefault("suffix-space", List.of("off")).getFirst();
                        main.getConfigFile().setPrefixSpaceEnabled(prefixSpace != null && prefixSpace.equals("on"));
                        main.getConfigFile().setSuffixSpaceEnabled(suffixSpace != null && suffixSpace.equals("on"));
                        return newFixedLengthResponse(PageBuilder.getManagePlguinPage(main));
                    } catch (Exception e) {
                        return newFixedLengthResponse(PageBuilder.getManagePlguinPage(main));
                    }
                }
            }
            if (uri.startsWith("/groups/") && (uri.endsWith("/settings") || uri.endsWith("/settings/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String groupName = uri.split("/")[2];
                Group g = main.getGroupHandler().getGroup(groupName);
                if(g == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String op = session.getParameters().getOrDefault("group-op", List.of("off")).getFirst();
                    String def = session.getParameters().getOrDefault("group-default", List.of("off")).getFirst();
                    String prefix = session.getParameters().getOrDefault("group-prefix", List.of("")).getFirst();
                    String suffix = session.getParameters().getOrDefault("group-suffix", List.of("")).getFirst();
                    String rank = session.getParameters().getOrDefault("group-rank", List.of("0")).getFirst();
                    g.setOp(op != null && op.equals("on"));
                    if(def != null && def.equals("on")) {
                        main.getGroupHandler().setDefault(g);
                    } else if(main.getGroupHandler().getDefault() != null && main.getGroupHandler().getDefault().getName().equals(g.getName())) {
                        main.getGroupHandler().removeDefault();
                    }
                    g.setPrefix(Objects.requireNonNullElse(prefix, ""));
                    g.setSuffix(Objects.requireNonNullElse(suffix, ""));
                    if(rank != null) {
                        try {
                            int r = Integer.parseInt(rank);
                            g.setRank(r);
                        } catch (NumberFormatException ignored) {
                            main.getLogger().warning("Could not parse rank: " + rank);
                        }
                    }
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                } catch (Exception e) {
                    main.getLogger().severe("Could not parse group settings form: " + e.getMessage());
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                }
            }
            if (uri.startsWith("/groups/") && (uri.endsWith("/permissions") || uri.endsWith("/permissions/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String groupName = uri.split("/")[2];
                Group g = main.getGroupHandler().getGroup(groupName);
                if(g == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String addPerm = session.getParameters().getOrDefault("group-permissions", null).getFirst();
                    GroupHandler gh = main.getGroupHandler();
                    LinkedList<String> newPerms = new LinkedList<>();
                    addPerm.lines().forEach(perm -> {
                        if(perm != null && !perm.isEmpty()) {
                            newPerms.add(perm);
                        }
                    });
                    gh.setPermissions(newPerms, g);
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                }
            } else if (uri.startsWith("/groups/") && (uri.endsWith("/negated-permissions") || uri.endsWith("/negated-permissions/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String groupName = uri.split("/")[2];
                Group g = main.getGroupHandler().getGroup(groupName);
                if(g == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String addPerm = session.getParameters().getOrDefault("group-negated-permissions", null).getFirst();
                    GroupHandler gh = main.getGroupHandler();
                    LinkedList<String> newPerms = new LinkedList<>();
                    addPerm.lines().forEach(perm -> {
                        if(perm != null && !perm.isEmpty()) {
                            newPerms.add(perm);
                        }
                    });
                    gh.setNegatedPermissions(newPerms, g);
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                }
            } else if (uri.startsWith("/groups/") && (uri.endsWith("/add-player") || uri.endsWith("/add-player/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String groupName = uri.split("/")[2];
                Group g = main.getGroupHandler().getGroup(groupName);
                if(g == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String playerName = session.getParameters().getOrDefault("player-name", null).getFirst();
                    PlayerHandler ph = main.getPlayerHandler();
                    if(playerName != null && !playerName.isEmpty() && ph.getPlayers().contains(playerName)) {
                        ph.addGroup(g, playerName);
                    }
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                }
            } else if (uri.startsWith("/groups/") && (uri.endsWith("/remove-player") || uri.endsWith("/remove-player/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String groupName = uri.split("/")[2];
                Group g = main.getGroupHandler().getGroup(groupName);
                if(g == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String playerName = session.getParameters().getOrDefault("remove-player-name", null).getFirst();
                    PlayerHandler ph = main.getPlayerHandler();
                    if(playerName != null && !playerName.isEmpty() && ph.getPlayers().contains(playerName) && ph.getGroups(playerName).contains(g.getName())) {
                        ph.removeGroup(g, playerName);
                    }
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getGroupPage(main, g.getName()));
                }
            } else if (uri.startsWith("/groups/") && (uri.endsWith("/delete") || uri.endsWith("/delete/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                String groupName = uri.split("/")[2];
                Group g = main.getGroupHandler().getGroup(groupName);
                if(g == null) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                main.getGroupHandler().deleteGroup(g);
            }  else if (uri.startsWith("/players/") && (uri.endsWith("/permissions") || uri.endsWith("/permissions/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String playerName = uri.split("/")[2];
                if(playerName == null || playerName.isEmpty() || !main.getPlayerHandler().getPlayers().contains(playerName)) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String addPerm = session.getParameters().getOrDefault("player-permissions", null).getFirst();
                    PlayerHandler ph = main.getPlayerHandler();
                    LinkedList<String> newPerms = new LinkedList<>();
                    addPerm.lines().forEach(perm -> {
                        if(perm != null && !perm.isEmpty()) {
                            newPerms.add(perm);
                        }
                    });
                    ph.setPermissions(newPerms, playerName);
                    return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
                }
            } else if (uri.startsWith("/players/") && (uri.endsWith("/negated-permissions") || uri.endsWith("/negated-permissions/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String playerName = uri.split("/")[2];
                if(playerName == null || playerName.isEmpty() || !main.getPlayerHandler().getPlayers().contains(playerName)) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String addPerm = session.getParameters().getOrDefault("player-negated-permissions", null).getFirst();
                    PlayerHandler ph = main.getPlayerHandler();
                    LinkedList<String> newPerms = new LinkedList<>();
                    addPerm.lines().forEach(perm -> {
                        if(perm != null && !perm.isEmpty()) {
                            newPerms.add(perm);
                        }
                    });
                    ph.setNegatedPermissions(newPerms, playerName);
                    return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
                }
            }  else if (uri.startsWith("/players/") && (uri.endsWith("/name-color") || uri.endsWith("/name-color/"))) {
                HashMap<String, String> headers = new HashMap<>(session.getHeaders());
                String cookie = headers.getOrDefault("cookie", "");
                if (!cookie.contains("loggedin=true")) {
                    return newFixedLengthResponse(PageBuilder.getLoginPage());
                }
                HashMap<String, String> postData = new HashMap<>();
                String playerName = uri.split("/")[2];
                if(playerName == null || playerName.isEmpty() || !main.getPlayerHandler().getPlayers().contains(playerName)) {
                    return newFixedLengthResponse(PageBuilder.getNotFoundPage());
                }
                try {
                    session.parseBody(postData);
                    String nameColor = session.getParameters().getOrDefault("name-color", null).getFirst();
                    PlayerHandler ph = main.getPlayerHandler();
                    if(nameColor != null) {
                        ph.setPlayerNameColor(playerName, ChatColor.getByChar(nameColor));
                    } else {
                        ph.setPlayerNameColor(playerName, ChatColor.RESET);
                    }
                    return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
                } catch (Exception e) {
                    return newFixedLengthResponse(PageBuilder.getPlayerPage(main, playerName));
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
