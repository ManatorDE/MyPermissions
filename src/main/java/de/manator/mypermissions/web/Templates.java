package de.manator.mypermissions.web;

import java.util.Comparator;
import java.util.LinkedList;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;

public class Templates {

    public static String getHeader(String title) {
        return "<header><a href=\"/\"><img class=\"logo\" src=\"https://dev.manator.de/rss/MyPermissions_2.png\" alt=\"MP-Logo\"><h1>" + title + "</h1></a></header>";
    }

    public static String getHead(String title) {
        return "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><link rel=\"stylesheet\" href=\"/style/style.css\"><link rel=\"shortcut icon\" href=\"https://test.manator.de/resources/icon.svg\" type=\"image/x-icon\"><title>" + title + "</title></head>";
    }

    public static String startHtml() {
        return "<!DOCTYPE html>"
                + "<html lang=\"en\">";
    }

    public static String endHtml() {
        return "</html>";
    }

    public static String getBody(String title, String path, Main main) {
        String s = "<body>";
        s+= getHeader(title);
        s+= "<div class=\"content-div\">";
        if(path.contains("groups")) {
            if(path.equals("/groups") || path.equals("/groups/")) {
                s+= getGroups(main);
            } else if(path.equals("/groups/create")) {
                s+= getGroupCreate(main);
            } else {
                s+= getGroupOptions(path.split("/")[2], main);
            }
        } else if(path.contains("permissions")) {
            if(path.equals("/permissions") || path.equals("/permissions/")) {
                s+= getPermissions(main);
            } else {
                s+= getPermissionsOptions(path.split("/")[2], main);
            }

        } else {
            s+=getMain();
        }
        s+= "</div>";
        s+= getFooter(true);
        s+="</body>";
        return s;   
    }

    public static String getLoginBody(String title, boolean cookies) {
        String s = "<body>" + getHeader(title) + "<div class=\"login-div\"><h2>Login</h2><form action=\"/\" method=\"post\"><label for=\"user\">Username: <input type=\"text\" name=\"user\" id=\"user\" placeholder=\"Username\"></label><label for=\"password\">Password: <input type=\"password\" name=\"password\" id=\"password\" placeholder=\"password\"></label><button type=\"submit\">Login</button></form></div>";
        if(!cookies) s += getCookies();
        s += getFooter(false);
        s+= "</body>";
        return s;

    }

    public static String getFooter(boolean loggedIn) {
        String s = "<footer>";
        s+= "&copy; 2024 ManatorDE <br> Contact: <a href=\"mailto:development@manator.de\">development@manator.de</a>";
        if(loggedIn) s+= "<br> <a href=\"/?action=logout\">Logout</a>";
        s+= "</footer>";
        return s;
    }

    public static String getGroupOptions(String group, Main main) {
        String s = "<h2>Group - " + group + "</h2>";
        s+="<script>function detailsClick(el) {var det = document.getElementsByTagName('details');for(let i = 0; i < det.length; i++) {var details = det[i];if(details != el) {details.removeAttribute('open');}}}</script>";
        s+="<details onclick=\"detailsClick(this)\"><summary><h3>Config</h3></summary>";
        s+="<form action=\"\" method=\"post\">";
        s+="<input type=\"hidden\" name=\"group\" value=\"" + group + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"config\">";
        if(main != null) {
            GroupHandler gh = main.getGroupHandler();
            Group g = gh.getGroup(group);
            String checked = g.isOp() ? " checked" : "";
            String defaultChecked = "";
            if(gh.getDefault() != null) {
                defaultChecked = gh.getDefault().equals(g) ? " checked" : "";
            }
            s+="<label for=\"op\">OP: <input type=\"checkbox\" name=\"op\" id=\"op\"" + checked + "></label>";
            s+="<label for=\"default\">Default group: <input type=\"checkbox\" name=\"default\" id=\"default\"" + defaultChecked + "></label>";
            s+="<label for=\"rank\">Rank: <input type=\"number\" name=\"rank\" id=\"rank\" min=\"0\" max=\"10\" value=\"" + g.getRank() + "\"></label>";
            String prefix = g.getPrefix() != null ? g.getPrefix() : "";
            s+="<label for=\"prefix\">Prefix: <input type=\"text\" name=\"prefix\" id=\"prefix\" value=\"" + prefix + "\"></label>";
            String suffix = g.getSuffix() != null ? g.getSuffix() : "";
            s+="<label for=\"suffix\">Suffix: <input type=\"text\" name=\"suffix\" id=\"suffix\" value=\"" + suffix + "\"></label>";
        } else {
            s+="<label for=\"op\">OP: <input type=\"checkbox\" name=\"op\" id=\"op\"></label>";
            s+="<label for=\"rank\">Rank: <input type=\"number\" name=\"rank\" id=\"rank\" min=\"0\" max=\"10\" value=\"0\"></label>";
            s+="<label for=\"prefix\">Prefix: <input type=\"text\" name=\"prefix\" id=\"prefix\" value=\"test\"></label>";
            s+="<label for=\"suffix\">Suffix: <input type=\"text\" name=\"suffix\" id=\"suffix\" value=\"tset\"></label>";
        }
        s+="<button type=\"submit\">Save config!</button></form></details>";

        s+="<details onclick=\"detailsClick(this)\">";
        s+="<summary><h3>Permissions</h3></summary>";
        s+="<form action=\"\" method=\"post\"><label for=\"permissions\">Permissions:</label>";
        s+="<input type=\"hidden\" name=\"group\" value=\"" + group + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"perms\">";
        s+="<textarea name=\"permissions\" id=\"permissions\">";
        if(main != null) {
            GroupHandler gh = main.getGroupHandler();

            for(String perm : gh.getPermissions(gh.getGroup(group))) {
                s+=perm + "\r\n";
            }
        } else {
            s+="perm1\r\n";
        }

        s+="</textarea><button type=\"submit\">Save permissions!</button></form></details>";
        s+="<details onclick=\"detailsClick(this)\">";
        s+="<summary><h3>Negated Permissions</h3></summary>";
        s+="<form action=\"\" method=\"post\"><label for=\"permissions\">Negated Permissions:</label>";
        s+="<input type=\"hidden\" name=\"group\" value=\"" + group + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"nperms\">";
        s+="<textarea name=\"npermissions\" id=\"npermissions\">";
        if(main != null) {
            GroupHandler gh = main.getGroupHandler();

            for(String perm : gh.getNegatedPermissions(gh.getGroup(group))) {
                s+=perm + "\r\n";
            }
        } else {
            s+="perm1\r\n";
        }

        s+="</textarea><button type=\"submit\">Save negated permissions!</button></form></details>";
        s+="<details onclick=\"detailsClick(this)\"><summary><h3>Add Player</h3></summary>";
        s+="<form action=\"\" method=\"post\"><label for=\"player\">Select player:</label>";
        s+="<input type=\"hidden\" name=\"group\" value=\"" + group + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"playeradd\">";
        s+= "<select name=\"player\" id=\"player\">";
        if(main != null) {
            PlayerHandler ph = main.getPlayerHandler();
            LinkedList<String> players = ph.getPlayers();
            players.sort(Comparator.naturalOrder());
            for(String player : players) {
                if(!ph.getGroups(player).contains(group)) {
                    s+="<option value=\"" + player + "\">" + player + "</option>";
                }
            }
        }
        s+="</select><button type=\"submit\">Add selected player</button></form></details>";
        s+="<details onclick=\"detailsClick(this)\"><summary><h3>Remove Player</h3></summary>";
        s+="<form action=\"\" method=\"post\"><label for=\"player\">Select player:</label>";
        s+="<input type=\"hidden\" name=\"group\" value=\"" + group + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"playerrm\">";
        s+= "<select name=\"player\" id=\"player\">";
        if(main != null) {
            PlayerHandler ph = main.getPlayerHandler();
            LinkedList<String> players = ph.getPlayers();
            players.sort(Comparator.naturalOrder());
            for(String player : players) {
                if(ph.getGroups(player).contains(group)) {
                    s+="<option value=\"" + player + "\">" + player + "</option>";
                }
            }
        }
        s+="</select><button type=\"submit\">Remove selected player</button></form></details>";
        s+="<form action=\"/groups\" method=\"post\">"
            + "    <input type=\"hidden\" name=\"action\" value=\"deletegroup\">"
            + "    <input type=\"hidden\" name=\"group\" value=\"" + group + "\">"
            + "    <button type=\"submit\" class=\"delete\">Delete group!</button>"
            + "</form>";
        return s;
    }

    private static String getGroupCreate(Main main) {
        String s = "<h2>Create group!</h2><form action=\"/groups\" method=\"post\"><label for=\"name\">Name: <input type=\"text\" name=\"name\" id=\"name\" placeholder=\"Group\" required></label>";
        s+="<label for=\"super\">Supergroup: <select name=\"super\" id=\"super\">";
        s+="<option value=\"nogroup\">No selection</option>";
        if(main != null) {
            GroupHandler gh = main.getGroupHandler();
            for(Group g : gh.getGroups()) {
                s+= "<option value=\"" + g.getName() + "\">" + g.getName() + "</option>";
            }
        } else {
            s+= "<option value=\"test\">test</option>";
        }
        s+= "</select></label><input type=\"hidden\" name=\"action\" value=\"creategroup\"><button type=\"submit\" class=\"create\">Create group!</button></form>";
        return s;
    }

    public static String getPermissionsOptions(String player, Main main) {
        String s = "<h2>Player - " + player + "</h2>";
        s+="<details onclick=\"detailsClick(this)\">";
        s+="<summary><h3>Permissions</h3></summary>";
        s+="<form action=\"\" method=\"post\"><label for=\"permissions\">Permissions:</label>";
        s+="<input type=\"hidden\" name=\"player\" value=\"" + player + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"permsplayer\">";
        s+="<textarea name=\"permissions\" id=\"permissions\">";
        if(main != null) {
            PlayerHandler ph = main.getPlayerHandler();

            for(String perm : ph.getPermissions(player)) {
                s+=perm + "\r\n";
            }
        } else {
            s+="perm1\r\n";
        }

        s+="</textarea><button type=\"submit\">Save permissions!</button></form></details>";
        s+="<details onclick=\"detailsClick(this)\">";
        s+="<summary><h3>Negated Permissions</h3></summary>";
        s+="<form action=\"\" method=\"post\"><label for=\"permissions\">Negated Permissions:</label>";
        s+="<input type=\"hidden\" name=\"player\" value=\"" + player + "\">";
        s+="<input type=\"hidden\" name=\"action\" value=\"npermsplayer\">";
        s+="<textarea name=\"npermissions\" id=\"npermissions\">";

        if(main != null) {
            PlayerHandler ph = main.getPlayerHandler();
            for(String perm : ph.getNegatedPermissions(player)) {
                s+=perm + "\r\n";
            }
        } else {
            s+="perm1\r\n";
        }

        s+="</textarea><button type=\"submit\">Save negated permissions!</button></form></details>";
        return s;
    }

    public static String getGroups(Main main) {
        String s = "<h2>Groups</h2>";
        if(main != null) {
            GroupHandler gh = main.getGroupHandler();
            s+="<a class=\"button create\" href=\"/groups/create\">Create a group!</a>";
            for(Group g : gh.getGroups()) {
                s+="<a class=\"button\" href=\"/groups/" + g.getName() + "\">" + g.getName() + "</a>";
            }
        } else {
            s+="<a class=\"button\" href=\"/groups/test\">test</a>";
        }
        return s;
    }

    public static String getPermissions(Main main) {
        String s = "<h2>Players</h2>";
        if(main != null) {
            PlayerHandler ph = main.getPlayerHandler();
            LinkedList<String> players = ph.getPlayers();
            players.sort(Comparator.naturalOrder());
            for(String pl : players) {
                s+="<a class=\"button\" href=\"/permissions/" + pl + "\">" + pl + "</a>";
            }
        } else {
            s+="<a class=\"button\" href=\"/permissions/test\">test</a>";
        }
        return s;
    }

    public static String getCookies() {
        return "<div class=\"cookie-bg\">\r\n"
                + "    <div class=\"cookie-div\">\r\n"
                + "        <h2>We use cookies!</h2>\r\n"
                + "        <form action=\"\" method=\"post\">\r\n"
                + "            <input type=\"hidden\" name=\"cookies\" value=\"1\">\r\n"
                + "            <label>All cookies this website uses are essential for it to work. To use this website you have to allow us to use them!</label>\r\n"
                + "            <button type=\"submit\">Allow use of cookies!</button>\r\n"
                + "        </form>\r\n"
                + "    </div>\r\n"
                + "</div>";
    }

    public static String getStyles() {
        return "html, body {\r\n"
                + "    margin: 0;\r\n"
                + "}\r\n"
                + "\r\n"
                + "body {\r\n"
                + "    background-color: #eee;\r\n"
                + "    font-family: monospace;\r\n"
                + "    font-size: 1.1rem;\r\n"
                + "    margin-bottom: 100px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "header {\r\n"
                + "    position: sticky;\r\n"
                + "    top: 0px;\r\n"
                + "    text-align: center;\r\n"
                + "    z-index: 101;\r\n"
                + "    width: 100%;\r\n"
                + "    height: fit-content;\r\n"
                + "    background-image: linear-gradient(#fff, #eee);\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "    margin-bottom: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "footer {\r\n"
                + "    position: fixed;\r\n"
                + "    bottom: 0;\r\n"
                + "    left: 0;\r\n"
                + "    z-index: 100;\r\n"
                + "    width: 100%;\r\n"
                + "    height: fit-content;\r\n"
                + "    text-align: center;\r\n"
                + "    background-image: linear-gradient(#eee, #fff);\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "    padding-left: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".logo {\r\n"
                + "    width: 50px;\r\n"
                + "    display: inline-block;\r\n"
                + "    margin-bottom: -15px;\r\n"
                + "    pointer-events: none;\r\n"
                + "}\r\n"
                + "\r\n"
                + "header h1 {\r\n"
                + "    display: inline-block;\r\n"
                + "}\r\n"
                + "\r\n"
                + "header a {\r\n"
                + "    text-decoration: none;\r\n"
                + "    color: #000;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".cookie-bg {\r\n"
                + "    z-index: 200;\r\n"
                + "    width: 100%;\r\n"
                + "    height: 100%;\r\n"
                + "    position: fixed;\r\n"
                + "    top: 0px;\r\n"
                + "    left: 0px;\r\n"
                + "    background-color: #fff5;\r\n"
                + "    backdrop-filter: blur(10px);\r\n"
                + "}\r\n"
                + "\r\n"
                + ".cookie-div {\r\n"
                + "    margin: 50px;\r\n"
                + "    padding: 10px;\r\n"
                + "    width: fit-content;\r\n"
                + "    height: fit-content;\r\n"
                + "    border-radius: 10px;\r\n"
                + "    background-color: #fff;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #000;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".login-div {\r\n"
                + "    margin: auto;\r\n"
                + "    padding: 10px;\r\n"
                + "    text-align: center;\r\n"
                + "    background-color: #fff;\r\n"
                + "    width: fit-content;\r\n"
                + "    border-radius: 10px;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".login-div form label {\r\n"
                + "    display: block;\r\n"
                + "    margin-top: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".content-div {\r\n"
                + "    max-width: 800px;\r\n"
                + "    width: 90%;\r\n"
                + "    background-color: #fff;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "    margin: auto;\r\n"
                + "    padding: 10px;\r\n"
                + "    border-radius: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "input[type=text], input[type=password] {\r\n"
                + "    border: none;\r\n"
                + "    border-radius: 5px;\r\n"
                + "    background-color: #fff;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "    color: #000;\r\n"
                + "    font-size: 1.1rem;\r\n"
                + "}\r\n"
                + "\r\n"
                + "button[type=submit] {\r\n"
                + "    border: none;\r\n"
                + "    border-radius: 5px;\r\n"
                + "    background-color: #fff;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "    color: #000;\r\n"
                + "    font-size: 1.1rem;\r\n"
                + "    display: block;\r\n"
                + "    margin: auto;\r\n"
                + "    margin-top: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "button[type=submit]:hover {\r\n"
                + "    box-shadow: 0px 0px 10px 0px #0005;\r\n"
                + "    transition: all 0.2s;\r\n"
                + "    cursor: pointer;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".create, button[type=submit].create, .button.create {\r\n"
                + "    background-color: #094;\r\n"
                + "    color: #fff;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".delete, button[type=submit].delete, .button.delete {\r\n"
                + "    background-color: #900;\r\n"
                + "    color: #fff;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".button {\r\n"
                + "    text-decoration: none;\r\n"
                + "    color: #000;\r\n"
                + "    background-color: #fff;\r\n"
                + "    padding: 10px;\r\n"
                + "    border: none;\r\n"
                + "    border-radius: 5px;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "    display: block;\r\n"
                + "    margin-bottom: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + ".button:hover {\r\n"
                + "    box-shadow: 0px 0px 10px 0px #0008;\r\n"
                + "    transition: all 0.2s;\r\n"
                + "}\r\n"
                + "\r\n"
                + "summary h3 {\r\n"
                + "    display: inline-block;\r\n"
                + "}\r\n"
                + "\r\n"
                + "details {\r\n"
                + "    border-radius: 10px;\r\n"
                + "    background-color: #fff;\r\n"
                + "    margin-bottom: 10px;\r\n"
                + "    padding: 0px 20px 10px 20px;\r\n"
                + "    box-shadow: 0px 0px 5px 0px #0005;\r\n"
                + "}\r\n"
                + "\r\n"
                + "details:hover {\r\n"
                + "    cursor: pointer;\r\n"
                + "    box-shadow: 0px 0px 10px 0px #0005;\r\n"
                + "    transition: all 0.2s;\r\n"
                + "}\r\n"
                + "\r\n"
                + "details form {\r\n"
                + "    padding: 10px;\r\n"
                + "    border: solid 1px #ddd;\r\n"
                + "    border-radius: 10px;\r\n"
                + "    font-size: 1.1rem;\r\n"
                + "    text-align: center;\r\n"
                + "}\r\n"
                + "\r\n"
                + "details form label {\r\n"
                + "    display: block;\r\n"
                + "    margin-bottom: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "textarea {\r\n"
                + "    resize: none;\r\n"
                + "    width: 80%;\r\n"
                + "    height: 400px;\r\n"
                + "    background-color: #eee;\r\n"
                + "    padding: 10px;\r\n"
                + "    border-radius: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "select {\r\n"
                + "    margin-top: 10px;\r\n"
                + "}\r\n"
                + "\r\n"
                + "select:hover, option:hover {\r\n"
                + "    cursor: pointer;\r\n"
                + "}\r\n"
                + "\r\n"
                + "@media only screen and (max-width: 880px) {\r\n"
                + "    header h1 {\r\n"
                + "        display: none;\r\n"
                + "    }\r\n"
                + "\r\n"
                + "    .logo {\r\n"
                + "        margin: 10px;\r\n"
                + "        pointer-events: all;\r\n"
                + "    }\r\n"
                + "}";
    }

    public static String getMain() {
        return "<h2>Options</h2><a class=\"button\" href=\"/groups\">Manage groups</a><a class=\"button\" href=\"/permissions\">Manage players</a><a class=\"button\" href=\"/reload\">Reload MyPermissions</a>";
    }
}
