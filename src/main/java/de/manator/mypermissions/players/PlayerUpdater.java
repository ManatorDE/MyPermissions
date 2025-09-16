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
    private final Main main;

    /**
     * A reference to the PlayerHandler object of MyPermissions
     */
    private final PlayerHandler ph;

    /**
     * A reference to the GroupHandler object of MyPermissions
     */
    private final GroupHandler gh;

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
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(main, this);
            return;
        }
        main.getPerms().clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != null) {
                PermissionAttachment attachment = p.addAttachment(main);
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
                                if(perm != null)
                                    attachment.setPermission(perm, true);
                            }
                            for (String nperm : gh.getNegatedPermissions(gh.getGroup(gr))) {
                                if(nperm != null)
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
                        if(Bukkit.getScoreboardManager() == null) {
                            main.getLogger().severe("Could not get ScoreboardManager");
                            return;
                        }
                        Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

                        if (s.getTeam(prefix.getName()) == null) {
                            t = Bukkit.getScoreboardManager().getMainScoreboard()
                                    .registerNewTeam(prefix.getName());
                        } else {
                            t = s.getTeam(prefix.getName());
                        }
                        if (t == null) {
                            main.getLogger().severe("Could not create or get team " + prefix.getName());
                            return;
                        }
                        if (prefix.getPrefix() != null) {
                            t.setPrefix(prefix.getPrefix());
                            if(main.getConfigFile().isPrefixSpaceEnabled()) {
                                name += prefix.getPrefix() + " ";
                            } else {
                                name += prefix.getPrefix();
                            }
                        }
                        name += ChatColor.WHITE + p.getName();
                        if (prefix.getSuffix() != null) {
                            t.setPrefix(prefix.getSuffix());
                            if(main.getConfigFile().isSuffixSpaceEnabled()) {
                                name += " " + prefix.getSuffix();
                            } else {
                                name += prefix.getSuffix();
                            }
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
                main.getPerms().put(p.getUniqueId(), attachment);
                p.updateCommands();
            }
        }
    }

}
