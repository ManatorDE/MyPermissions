package de.manator.mypermissions.commands;

import java.util.LinkedList;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * The CommandExecutor of the group command
 * @author ManatorDE
 */
public class GroupCMD implements CommandExecutor {

	/**
	 * A reference to the Main object of MyPermissions
	 */
	private final Main main;
	
	/**
	 * A reference to the GroupHandler object of MyPermissions
	 */
	private final GroupHandler gh;
	
	/**
	 * A reference to the PlayerHandler object of MyPermissions
	 */
	private final PlayerHandler ph;

	/**
	 * The constructor of GroupCMD
	 * @param main A reference to the Main object of MyPermissions
	 */
	public GroupCMD(Main main) {
		this.main = main;
		gh = this.main.getGroupHandler();
		ph = this.main.getPlayerHandler();
	}

	/**
	 * A method that gets called when a command was send
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        switch (sender) {
            case Player p -> {

                if (command.getName().equalsIgnoreCase("group")) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("getdefault")) {
                            CMD.sendMessage(p, ChatColor.GREEN + "The default group is: " + ChatColor.GOLD + gh.getDefault().getName());
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("create")) {
                            if (gh.addGroup(args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was created!");
                                main.getConfigs().updateGroups();
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group already exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("delete")) {
                            if (gh.deleteGroup(gh.getGroup(args[0]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group doesn't exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("setdefault")) {
                            if (gh.setDefault(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set as default group!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Setting the default group failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("getprefix")) {
                            String pre = gh.getPrefix(gh.getGroup(args[1]));
                            if (pre != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: " + ChatColor.RESET + pre);
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to get the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("getsuffix")) {
                            String suf = gh.getSuffix(gh.getGroup(args[1]));
                            if (suf != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: " + ChatColor.RESET + suf);
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to get the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("deleteprefix")) {
                            if (gh.deletePrefix(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed deleting the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("deletesuffix")) {
                            if (gh.deleteSuffix(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed deleting the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("getplayers")) {
                            Group g = gh.getGroup(args[1]);
                            if (g != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group contains these players:");
                                for (String s : getPlayers(g)) {
                                    CMD.sendMessage(p, "-" + s);
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to list the players of the group!");
                            }
                        } else if (args[0].equalsIgnoreCase("getgroups")) {
                            if (ph.getPlayers().contains(args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is in these groups:");
                                for (String s : ph.getGroups(args[1])) {
                                    CMD.sendMessage(p, "-" + s);
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to list the players groups!");
                            }
                        } else if (args[0].equalsIgnoreCase("config")) {
                            if (gh.getGroup(args[1]) != null && main.getConfigs().getGroupConfig(args[1]) != null) {
                                p.openInventory(main.getConfigs().getGroupConfig(args[1]));
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to open the config!");
                            }
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (gh.addPermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was added to the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Adding the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (gh.removePermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the group " + ChatColor.GOLD
                                        + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("addplayer")) {
                            if (ph.addGroup(gh.getGroup(args[2]), args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "Added " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to the group " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Adding the player failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("removeplayer")) {
                            if (ph.removeGroup(gh.getGroup(args[2]), args[1])) {
                                if (gh.getGroup(args[2]).isOp()) {
                                    boolean deopped = false;
                                    for (Player pl : Bukkit.getOnlinePlayers()) {
                                        if (pl.getName().equalsIgnoreCase(args[1])) {
                                            pl.setOp(false);
                                            deopped = true;
                                            break;
                                        }
                                    }
                                    if (!deopped) {
                                        for (OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                                            if (Objects.requireNonNull(pl.getName()).equalsIgnoreCase(args[1])) {
                                                pl.setOp(false);
                                                break;
                                            }
                                        }
                                    }
                                }
                                CMD.sendMessage(p, ChatColor.GREEN + "Removed " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " from the group " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the player failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("setop")) {
                            boolean op = Boolean.parseBoolean(args[2]);
                            if (gh.setOp(gh.getGroup(args[1]), op)) {
                                if (op) {
                                    CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was opped!");
                                } else {
                                    CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deopped!");
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to op/deop the group!");
                            }
                        } else if (args[0].equalsIgnoreCase("setprefix")) {
                            if (gh.setPrefix(gh.getGroup(args[1]), ChatColor.translateAlternateColorCodes('&', args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set to " + ChatColor.RESET
                                        + gh.getGroup(args[1]).getPrefix());
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to set the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("setsuffix")) {
                            if (gh.setSuffix(gh.getGroup(args[1]), ChatColor.translateAlternateColorCodes('&', args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set to " + ChatColor.RESET
                                        + gh.getGroup(args[1]).getSuffix());
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to set the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("create")) {
                            if (gh.addGroup(args[1], gh.getGroup(args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was created!");
                                main.getConfigs().updateGroups();
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group already exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("negate")) {
                            if (gh.negatePermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The negated permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was added to the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Negating the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("removenegation")) {
                            if (gh.removeNegatedPermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The negated permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the negated permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("setrank")) {
                            if (gh.setRank(gh.getGroup(args[1]), Integer.parseInt(args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "Set the rank of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Couldn't set the rank!");
                            }
                        }
                    }
                }
            }
            case ConsoleCommandSender p -> {

                if (command.getName().equalsIgnoreCase("group")) {
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("create")) {
                            if (gh.addGroup(args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was created!");
                                main.getConfigs().updateGroups();
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group already exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("delete")) {
                            if (gh.deleteGroup(gh.getGroup(args[0]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group doesn't exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("setdefault")) {
                            if (gh.setDefault(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set as default group!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Setting the default group failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("getprefix")) {
                            String pre = gh.getPrefix(gh.getGroup(args[1]));
                            if (pre != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: " + ChatColor.RESET + pre);
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to get the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("getsuffix")) {
                            String suf = gh.getSuffix(gh.getGroup(args[1]));
                            if (suf != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: " + ChatColor.RESET + suf);
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to get the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("deleteprefix")) {
                            if (gh.deletePrefix(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed deleting the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("deletesuffix")) {
                            if (gh.deleteSuffix(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed deleting the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("getplayers")) {
                            Group g = gh.getGroup(args[1]);
                            if (g != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group contains these players:");
                                for (String s : getPlayers(g)) {
                                    CMD.sendMessage(p, "-" + s);
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to list the players of the group!");
                            }
                        } else if (args[0].equalsIgnoreCase("getgroups")) {
                            if (ph.getPlayers().contains(args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is in these groups:");
                                for (String s : ph.getGroups(args[1])) {
                                    CMD.sendMessage(p, "-" + s);
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to list the players groups!");
                            }
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (gh.addPermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was added to the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Adding the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (gh.removePermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the group " + ChatColor.GOLD
                                        + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("addplayer")) {
                            if (ph.addGroup(gh.getGroup(args[2]), args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "Added " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to the group " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Adding the player failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("removeplayer")) {
                            if (ph.removeGroup(gh.getGroup(args[2]), args[1])) {
                                if (gh.getGroup(args[2]).isOp()) {
                                    boolean deopped = false;
                                    for (Player pl : Bukkit.getOnlinePlayers()) {
                                        if (pl.getName().equalsIgnoreCase(args[1])) {
                                            pl.setOp(false);
                                            deopped = true;
                                            break;
                                        }
                                    }
                                    if (!deopped) {
                                        for (OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                                            if (Objects.requireNonNull(pl.getName()).equalsIgnoreCase(args[1])) {
                                                pl.setOp(false);
                                                break;
                                            }
                                        }
                                    }
                                }
                                CMD.sendMessage(p, ChatColor.GREEN + "Removed " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " from the group " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the player failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("setop")) {
                            boolean op = Boolean.parseBoolean(args[2]);
                            if (gh.setOp(gh.getGroup(args[1]), op)) {
                                if (op) {
                                    CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was opped!");
                                } else {
                                    CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deopped!");
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to op/deop the group!");
                            }
                        } else if (args[0].equalsIgnoreCase("setprefix")) {
                            if (gh.setPrefix(gh.getGroup(args[1]), ChatColor.translateAlternateColorCodes('&', args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set to " + ChatColor.RESET
                                        + gh.getGroup(args[1]).getPrefix());
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to set the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("setsuffix")) {
                            if (gh.setSuffix(gh.getGroup(args[1]), ChatColor.translateAlternateColorCodes('&', args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set to " + ChatColor.RESET
                                        + gh.getGroup(args[1]).getSuffix());
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to set the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("create")) {
                            if (gh.addGroup(args[1], gh.getGroup(args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was created!");
                                main.getConfigs().updateGroups();
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group already exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("negate")) {
                            if (gh.negatePermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The negated permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was added to the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Negating the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("removenegation")) {
                            if (gh.removeNegatedPermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The negated permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the negated permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("setrank")) {
                            if (gh.setRank(gh.getGroup(args[1]), Integer.parseInt(args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "Set the rank of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Couldn't set the rank!");
                            }
                        }
                    }
                }
            }
            case BlockCommandSender p -> {

                if (command.getName().equalsIgnoreCase("group")) {
                    if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("create")) {
                            if (gh.addGroup(args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was created!");
                                main.getConfigs().updateGroups();
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group already exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("delete")) {
                            if (gh.deleteGroup(gh.getGroup(args[0]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group doesn't exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("setdefault")) {
                            if (gh.setDefault(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set as default group!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Setting the default group failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("getprefix")) {
                            String pre = gh.getPrefix(gh.getGroup(args[1]));
                            if (pre != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: " + ChatColor.RESET + pre);
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to get the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("getsuffix")) {
                            String suf = gh.getSuffix(gh.getGroup(args[1]));
                            if (suf != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is: " + ChatColor.RESET + suf);
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to get the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("deleteprefix")) {
                            if (gh.deletePrefix(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed deleting the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("deletesuffix")) {
                            if (gh.deleteSuffix(gh.getGroup(args[1]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deleted!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed deleting the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("getplayers")) {
                            Group g = gh.getGroup(args[1]);
                            if (g != null) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group contains these players:");
                                for (String s : getPlayers(g)) {
                                    CMD.sendMessage(p, "-" + s);
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to list the players of the group!");
                            }
                        } else if (args[0].equalsIgnoreCase("getgroups")) {
                            if (ph.getPlayers().contains(args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " is in these groups:");
                                for (String s : ph.getGroups(args[1])) {
                                    CMD.sendMessage(p, "-" + s);
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to list the players groups!");
                            }
                        }
                    } else if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (gh.addPermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was added to the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Adding the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (gh.removePermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the group " + ChatColor.GOLD
                                        + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("addplayer")) {
                            if (ph.addGroup(gh.getGroup(args[2]), args[1])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "Added " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to the group " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Adding the player failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("removeplayer")) {
                            if (ph.removeGroup(gh.getGroup(args[2]), args[1])) {
                                if (gh.getGroup(args[2]).isOp()) {
                                    boolean deopped = false;
                                    for (Player pl : Bukkit.getOnlinePlayers()) {
                                        if (pl.getName().equalsIgnoreCase(args[1])) {
                                            pl.setOp(false);
                                            deopped = true;
                                            break;
                                        }
                                    }
                                    if (!deopped) {
                                        for (OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
                                            if (Objects.requireNonNull(pl.getName()).equalsIgnoreCase(args[1])) {
                                                pl.setOp(false);
                                                break;
                                            }
                                        }
                                    }
                                }
                                CMD.sendMessage(p, ChatColor.GREEN + "Removed " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " from the group " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the player failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("setop")) {
                            boolean op = Boolean.parseBoolean(args[2]);
                            if (gh.setOp(gh.getGroup(args[1]), op)) {
                                if (op) {
                                    CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was opped!");
                                } else {
                                    CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was deopped!");
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to op/deop the group!");
                            }
                        } else if (args[0].equalsIgnoreCase("setprefix")) {
                            if (gh.setPrefix(gh.getGroup(args[1]), ChatColor.translateAlternateColorCodes('&', args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The prefix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set to " + ChatColor.RESET
                                        + gh.getGroup(args[1]).getPrefix());
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to set the prefix!");
                            }
                        } else if (args[0].equalsIgnoreCase("setsuffix")) {
                            if (gh.setSuffix(gh.getGroup(args[1]), ChatColor.translateAlternateColorCodes('&', args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The suffix of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was set to " + ChatColor.RESET
                                        + gh.getGroup(args[1]).getSuffix());
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Failed to set the suffix!");
                            }
                        } else if (args[0].equalsIgnoreCase("create")) {
                            if (gh.addGroup(args[1], gh.getGroup(args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " was created!");
                                main.getConfigs().updateGroups();
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The group already exists!");
                            }
                        } else if (args[0].equalsIgnoreCase("negate")) {
                            if (gh.negatePermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The negated permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was added to the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Negating the permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("removenegation")) {
                            if (gh.removeNegatedPermission(gh.getGroup(args[1]), args[2])) {
                                CMD.sendMessage(p,
                                        ChatColor.GREEN + "The negated permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Removing the negated permission failed!");
                            }
                        } else if (args[0].equalsIgnoreCase("setrank")) {
                            if (gh.setRank(gh.getGroup(args[1]), Integer.parseInt(args[2]))) {
                                CMD.sendMessage(p, ChatColor.GREEN + "Set the rank of the group " + ChatColor.GOLD + args[1] + ChatColor.GREEN + " to " + ChatColor.GOLD + args[2] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "Couldn't set the rank!");
                            }
                        }
                    }
                }
            }
            default -> {
                return true;
            }
        }
		
		main.reloadPlayers();
		
		return false;
	}

	/**
	 * Gets all players of a group
	 * @param g The group
	 * @return A list of all players of the group as Strings
	 */
	private LinkedList<String> getPlayers(Group g) {
		LinkedList<String> players = new LinkedList<>();
		for (String s : ph.getPlayers()) {
			if (ph.isInGroup(s, g)) {
				players.add(s);
			}
		}
		return players;
	}

}
