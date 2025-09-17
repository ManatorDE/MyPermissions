package de.manator.mypermissions.web;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import org.bukkit.Bukkit;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Scanner;

public class PageBuilder {
    public static String getGroupsPage(Main main) {
        String webbase = readResource("/webbase-min.html");
        String groups = readResource("/groups-min.html");
        String groupCard = readResource("/group-card-min.html");

        // Assamble group cards
        StringBuilder groupCards = new StringBuilder();
        main.getGroupHandler().getGroups().forEach(g -> {
            String card = groupCard.replace("<!group-name>", g.getName());
            groupCards.append(card);
        });

        // Assamble groups page
        groups = groups.replace("<!groups>", groupCards.toString());
        webbase = webbase.replace("<!main-slot>", groups);
        webbase = webbase.replace("<!head-slot>", "<title>Groups - MyPermissions</title>");
        return webbase;
    }

    public static String getPlayersPage(Main main) {
        String webbase = readResource("/webbase-min.html");
        String players = readResource("/players-min.html");
        String playerCard = readResource("/player-card-min.html");

        // Assamble player cards
        StringBuilder playerCards = new StringBuilder();
        main.getPlayerHandler().getPlayers().forEach(p -> {
            String card = playerCard.replace("<!player-name>", p);
            playerCards.append(card);
        });

        // Assamble players page
        players = players.replace("<!players>", playerCards.toString());
        webbase = webbase.replace("<!main-slot>", players);
        webbase = webbase.replace("<!head-slot>", "<title>Players - MyPermissions</title>");
        return webbase;
    }

    public  static String getIndexPage(Main main) {
        String webbase = readResource("/webbase-min.html");
        String index = readResource("/dashboard-min.html");
        String playerCard = readResource("/player-card-min.html");

        StringBuilder playerCards = new StringBuilder();
        Bukkit.getOnlinePlayers().forEach(p -> {
            String card = playerCard.replace("<!player-name>", p.getName());
            playerCards.append(card);
        });

        // Replace stats
        index = index.replace("<!active-players>", Bukkit.getOnlinePlayers().size() + "");
        index = index.replace("<!player-slots>", Bukkit.getMaxPlayers() + "");
        index = index.replace("<!total-players>", Bukkit.getOfflinePlayers().length + "");
        index = index.replace("<!player-list>", playerCards.toString());


        index = index.replace("<!total-groups>", main.getGroupHandler().getGroups().size() + "");
        index = index.replace("<!default-group>", main.getGroupHandler().getDefault() != null ? main.getGroupHandler().getDefault().getName() : "none");

        webbase = webbase.replace("<!main-slot>", index);
        webbase = webbase.replace("<!head-slot>", "<title>MyPermissions Web Interface</title>");
        return webbase;
    }

    public static String getPlayerPage(Main main, String playerName) {
        String webbase = readResource("/webbase-min.html");
        String playerPage = readResource("/manage-player-min.html");

        // Check if player exists
        if (!main.getPlayerHandler().getPlayers().contains(playerName)) {
            return webbase.replace("<!main-slot>", "Player not found").replace("<!head-slot>", "<title>Player not found - MyPermissions</title>");
        }
        // Assamble player settings
        playerPage = playerPage.replace("<!player-name>", playerName);
        StringBuilder permissions = new StringBuilder();
        main.getPlayerHandler().getPermissions(playerName).forEach(perm -> permissions.append(perm).append("\n"));
        StringBuilder negatedPermissions = new StringBuilder();
        main.getPlayerHandler().getNegatedPermissions(playerName).forEach(perm -> negatedPermissions.append(perm).append("\n"));
        playerPage = playerPage.replace("<!player-permissions>", permissions);
        playerPage = playerPage.replace("<!player-negated-permissions>", negatedPermissions);

        webbase = webbase.replace("<!head-slot>", "<title>Player " + playerName + " - MyPermissions</title>");
        webbase = webbase.replace("<!main-slot>", playerPage);
        return webbase;
    }

    public static String getGroupPage(Main main, String groupName) {
        String webbase = readResource("/webbase-min.html");
        String groupPage = readResource("/manage-group-min.html");

        Group g = main.getGroupHandler().getGroup(groupName);
        GroupHandler gh = main.getGroupHandler();
        if(g == null) {
            return webbase.replace("<!main-slot>", "Group not found").replace("<!head-slot>", "<title>Group not found - MyPermissions</title>");
        }

        // Assamble group setting
        groupPage = groupPage.replace("<!group-name>", groupName);
        StringBuilder permissions = new StringBuilder();
        gh.getPermissions(g).forEach(perm -> permissions.append(perm).append("\n"));
        StringBuilder negatedPermissions = new StringBuilder();
        gh.getNegatedPermissions(g).forEach(perm -> negatedPermissions.append(perm).append("\n"));
        groupPage = groupPage.replace("<!group-permissions>", permissions);
        groupPage = groupPage.replace("<!group-negated-permissions>", negatedPermissions);

        groupPage = groupPage.replace("<!group-rank>", g.getRank() + "");
        groupPage = groupPage.replace("<!group-op>", g.isOp() ? "checked" : "");
        Group dg = gh.getDefault();
        groupPage = groupPage.replace("<!group-default>", dg != null && dg.getName().equals(g.getName()) ? "checked" : "");
        groupPage = groupPage.replace("<!group-prefix>", g.getPrefix() == null ? "" : g.getPrefix());
        groupPage = groupPage.replace("<!group-suffix>", g.getSuffix() == null ? "" : g.getSuffix());
        LinkedList<String> playersInGroup = new LinkedList<>();
        LinkedList<String> playersNotInGroup = new LinkedList<>();
        main.getPlayerHandler().getPlayers().forEach(p -> {
            if(main.getPlayerHandler().getGroups(p).contains(g.getName())) {
                playersInGroup.add(p);
            } else {
                playersNotInGroup.add(p);
            }
        });
        StringBuilder playersInGroupSB = new StringBuilder();
        playersInGroup.forEach(p -> playersInGroupSB.append("<option value=\"").append(p).append("\">").append(p).append("</option>\n"));
        StringBuilder playersNotInGroupSB = new StringBuilder();
        playersNotInGroup.forEach(p -> playersNotInGroupSB.append("<option value=\"").append(p).append("\">").append(p).append("</option>\n"));
        groupPage = groupPage.replaceFirst("<!player-names>", playersNotInGroupSB.toString());
        groupPage = groupPage.replaceFirst("<!player-names>", playersInGroupSB.toString());

        webbase = webbase.replace("<!head-slot>", "<title>Group " + groupName + " - MyPermissions</title>");
        webbase = webbase.replace("<!main-slot>", groupPage);
        return webbase;
    }

    public static InputStream getLogoInputStream() {
        return PageBuilder.class.getResourceAsStream("/logo.png");
    }

    public static String getCreateGroupPage(Main main) {
        String webbase = readResource("/webbase-min.html");
        String createGroupPage = readResource("/create-group-min.html");

        StringBuilder superGroupOptions = new StringBuilder();
        main.getGroupHandler().getGroups().forEach(g -> {
            superGroupOptions.append("<option value=\"").append(g.getName()).append("\">").append(g.getName()).append("</option>\n");
        });

        createGroupPage = createGroupPage.replace("<!super-group-options>", superGroupOptions.toString());

        webbase = webbase.replace("<!head-slot>", "<title>Create Group - MyPermissions</title>");
        webbase = webbase.replace("<!main-slot>", createGroupPage);
        return webbase;
    }

    public static String getLoginPage() {
        String webbase = readResource("/webbase-min.html");
        String loginPage = readResource("/login-min.html");

        webbase = webbase.replace("<!head-slot>", "<title>Login - MyPermissions</title>");
        webbase = webbase.replace("<!main-slot>", loginPage);
        return webbase;
    }

    public static String getNotFoundPage() {
        String webbase = readResource("/webbase-min.html");

        webbase = webbase.replace("<!head-slot>", "<title>404 Not Found - MyPermissions</title>");
        webbase = webbase.replace("<!main-slot>", "<h2>404 not found</h2><p>The requested page could not be found.</p>");
        return webbase;
    }

    public static String getManagePlguinPage(Main main) {
        String webbase = readResource("/webbase-min.html");
        String managePluginPage = readResource("/manage-plugin-min.html");

        managePluginPage = managePluginPage.replace("<!prefix-space>", main.getConfigFile().isPrefixSpaceEnabled() + "");
        managePluginPage = managePluginPage.replace("<!suffix-space>", main.getConfigFile().isSuffixSpaceEnabled() + "");

        webbase = webbase.replace("<!head-slot>", "<title>Manage Plugin - MyPermissions</title>");
        webbase = webbase.replace("<!main-slot>", managePluginPage);
        return webbase;
    }

    private static String readResource(String resourcePath) {
        Bukkit.getLogger().info("Loading resource: " + resourcePath);
        try (InputStream in = PageBuilder.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                Bukkit.getLogger().severe("Resource " + resourcePath + " not found!");
                return "";
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not load resource " + resourcePath + ": " + e.getMessage());
            return "";
        }
    }
}
