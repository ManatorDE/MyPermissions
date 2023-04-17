package de.manator.mypermissions.players;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;

/**
 * A class used to asynchronously reload all player data
 * 
 * @author ManatorDE
 */
public class PlayerUpdater implements Runnable {

    /**
     * A reference to the Main object of MyPermissions
     */
    private Main main;

    /**
     * A reference to the PlayerHandler object of MyPermissions
     */
    private PlayerHandler ph;

    /**
     * A reference to the GroupHandler object of MyPermissions
     */
    private GroupHandler gh;

    /**
     * The constructor of PlayerUpdater
     * 
     * @param main A reference to the Main object of MyPermissions
     */
    public PlayerUpdater(Main main) {
        this.main = main;
        this.ph = main.getPlayerHandler();
        this.gh = main.getGroupHandler();
    }

    /**
     * The run method of runnable. Used to update all players permissions
     */
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != null) {
                PermissionAttachment attachment = main.getPerms().get(p.getUniqueId());
                if (attachment == null) {
                    attachment = p.addAttachment(main);
                    main.getPerms().put(p.getUniqueId(), attachment);
                } else {
                    attachment = p.addAttachment(main);
                    main.getPerms().replace(p.getUniqueId(), attachment);
                }
                if (ph.getPlayers().contains(p.getName())) {
                    Group prefix = null;
                    for (String gr : ph.getGroups(p.getName())) {
                        Group g = gh.getGroup(gr);
                        if (g != null) {
                            if (prefix == null || prefix.getRank() < g.getRank()) {
                                prefix = g;
                            }
                            if (g.isOp()) {
                                p.setOp(true);
                            }
                            for (String perm : gh.getPermissions(gh.getGroup(gr))) {
                                attachment.setPermission(perm, true);
                            }
                            for (String nperm : gh.getNegatedPermissions(gh.getGroup(gr))) {
                                attachment.setPermission(nperm, false);
                            }
                        }
                    }

                    for (String perm : ph.getPermissions(p.getName())) {
                        if (perm != null) {
                            attachment.setPermission(perm, true);
                        }
                    }
                    for (String nperm : ph.getNegatedPermissions(p.getName())) {
                        if (nperm != null) {
                            attachment.setPermission(nperm, false);
                        }
                    }
                    String name = "";
                    Team t = null;
                    if (prefix != null) {
                        Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

                        if (s.getTeam(prefix.getName()) == null) {
                            t = Bukkit.getScoreboardManager().getMainScoreboard()
                                    .registerNewTeam(prefix.getName());
                        } else {
                            t = s.getTeam(prefix.getName());
                        }

                        if (prefix.getPrefix() != null) {
                            t.setPrefix(prefix.getPrefix());
                            name += prefix.getPrefix();
                        }
                        name += ChatColor.WHITE + p.getName();
                        if (prefix.getSuffix() != null) {
                            t.setPrefix(prefix.getSuffix());
                            name += prefix.getSuffix();
                        }
                    }

                    if (t != null) {
                        t.addEntry(p.getName());
                        p.setCustomName(name);
                        p.setDisplayName(name);
                        p.setPlayerListName(name);
                        p.setCustomNameVisible(true);
                    }
                }
            }
        }
    }

}
