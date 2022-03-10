package de.manator.mypermissions.commands;

import java.util.ArrayList;

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

public class GroupCMD implements CommandExecutor {

	private Main main;
	private GroupHandler gh;
	private PlayerHandler ph;

	public GroupCMD(Main main) {
		this.main = main;
		gh = this.main.getGroupHandler();
		ph = this.main.getPlayerHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (command.getName().equalsIgnoreCase("group")) {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("getdefault")) {
						CMD.sendMessage(p, "§aThe default group is: §6" + gh.getDefault().getName());
					}
				} else if (args.length == 2) {
					if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1])) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
							main.getConfigs().updateGroups();
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if (args[0].equalsIgnoreCase("delete")) {
						if (gh.deleteGroup(gh.getGroup(args[0]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cThe group doesn't exists!");
						}
					} else if (args[0].equalsIgnoreCase("setdefault")) {
						if (gh.setDefault(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was set as default group!");
						} else {
							CMD.sendMessage(p, "§cSetting the default group failed!");
						}
					} else if (args[0].equalsIgnoreCase("getprefix")) {
						String pre = gh.getPrefix(gh.getGroup(args[1]));
						if (pre != null) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a is: §r" + pre);
						} else {
							CMD.sendMessage(p, "§cFailed to get the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("getsuffix")) {
						String suf = gh.getSuffix(gh.getGroup(args[1]));
						if (suf != null) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a is: §r" + suf);
						} else {
							CMD.sendMessage(p, "§cFailed to get the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("deleteprefix")) {
						if (gh.deletePrefix(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cFailed deleting the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("deletesuffix")) {
						if (gh.deleteSuffix(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cFailed deleting the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("getplayers")) {
						Group g = gh.getGroup(args[1]);
						if (g != null) {
							CMD.sendMessage(p, "§aThe group contains these players:");
							for (String s : getPlayers(g)) {
								CMD.sendMessage(p, "-" + s);
							}
						} else {
							CMD.sendMessage(p, "§cFailed to list the players of the group!");
						}
					} else if (args[0].equalsIgnoreCase("getgroups")) {
						if (ph.getPlayers().contains(args[1])) {
							CMD.sendMessage(p, "§aThe player §6" + args[1] + "§a is in these groups:");
							for (String s : ph.getGroups(args[1])) {
								CMD.sendMessage(p, "-" + s);
							}
						} else {
							CMD.sendMessage(p, "§cFailed to list the players groups!");
						}
					} else if(args[0].equalsIgnoreCase("config")) {
						if(gh.getGroup(args[1]) != null && main.getConfigs().getGroupConfig(args[1]) != null) {
							p.openInventory(main.getConfigs().getGroupConfig(args[1]));
						} else {
							CMD.sendMessage(p, "§cFailed to open the config!");
						}
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("add")) {
						if (gh.addPermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe permission §6" + args[2] + "§a was added to the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cAdding the permission failed!");
						}
					} else if (args[0].equalsIgnoreCase("remove")) {
						if (gh.removePermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + "§a was removed from the group §6"
									+ args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the permission failed!");
						}
					} else if (args[0].equalsIgnoreCase("addplayer")) {
						if (ph.addGroup(gh.getGroup(args[2]), args[1])) {
							CMD.sendMessage(p, "§aAdded §6" + args[1] + "§a to the group §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cAdding the player failed!");
						}
					} else if (args[0].equalsIgnoreCase("removeplayer")) {
						if (ph.removeGroup(gh.getGroup(args[2]), args[1])) {
							if(gh.getGroup(args[2]).isOp()) {
								boolean deopped = false;
								for(Player pl : Bukkit.getOnlinePlayers()) {
									if(pl.getName().equalsIgnoreCase(args[1])) {
										pl.setOp(false);
										deopped = true;
										break;
									}
								}
								if(!deopped) {
									for(OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
										if(pl.getName().equalsIgnoreCase(args[1])) {
											pl.setOp(false);
											deopped = true;
											break;
										}
									}
								}
							}
							CMD.sendMessage(p, "§aRemoved §6" + args[1] + "§a from the group §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the player failed!");
						}
					} else if (args[0].equalsIgnoreCase("setop")) {
						boolean op = Boolean.parseBoolean(args[2]);
						if (gh.setOp(gh.getGroup(args[1]), op)) {
							if (op) {
								CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was opped!");
							} else {
								CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deopped!");
							}
						} else {
							CMD.sendMessage(p, "§cFailed to op/deop the group!");
						}
					} else if (args[0].equalsIgnoreCase("setprefix")) {
						if (gh.setPrefix(gh.getGroup(args[1]), args[2].replace('&', '§'))) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a was set to §r"
									+ gh.getGroup(args[1]).getPrefix());
						} else {
							CMD.sendMessage(p, "§cFailed to set the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("setsuffix")) {
						if (gh.setSuffix(gh.getGroup(args[1]), args[2].replace('&', '§'))) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a was set to §r"
									+ gh.getGroup(args[1]).getSuffix());
						} else {
							CMD.sendMessage(p, "§cFailed to set the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1], gh.getGroup(args[2]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
							main.getConfigs().updateGroups();
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if(args[0].equalsIgnoreCase("negate")) {
						if(gh.negatePermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe negated permission §6" + args[2] + "§a was added to the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cNegating the permission failed!");
						}
					} else if(args[0].equalsIgnoreCase("removenegation")) {
						if(gh.removeNegatedPermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe negated permission §6" + args[2] + "§a was removed from the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the negated permission failed!");
						}
					} else if(args[0].equalsIgnoreCase("setrank")) {
						if(gh.setRank(gh.getGroup(args[1]), Integer.parseInt(args[2]))) {
							CMD.sendMessage(p, "§aSet the rank of the group §6" + args[1] + " §ato §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cCouldn't set the rank!");
						}
					}
				}
			}

		} else if (sender instanceof ConsoleCommandSender) {
			ConsoleCommandSender p = (ConsoleCommandSender) sender;

			if (command.getName().equalsIgnoreCase("group")) {
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1])) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
							main.getConfigs().updateGroups();
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if (args[0].equalsIgnoreCase("delete")) {
						if (gh.deleteGroup(gh.getGroup(args[0]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cThe group doesn't exists!");
						}
					} else if (args[0].equalsIgnoreCase("setdefault")) {
						if (gh.setDefault(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was set as default group!");
						} else {
							CMD.sendMessage(p, "§cSetting the default group failed!");
						}
					} else if (args[0].equalsIgnoreCase("getprefix")) {
						String pre = gh.getPrefix(gh.getGroup(args[1]));
						if (pre != null) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a is: §r" + pre);
						} else {
							CMD.sendMessage(p, "§cFailed to get the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("getsuffix")) {
						String suf = gh.getSuffix(gh.getGroup(args[1]));
						if (suf != null) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a is: §r" + suf);
						} else {
							CMD.sendMessage(p, "§cFailed to get the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("deleteprefix")) {
						if (gh.deletePrefix(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cFailed deleting the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("deletesuffix")) {
						if (gh.deleteSuffix(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cFailed deleting the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("getplayers")) {
						Group g = gh.getGroup(args[1]);
						if (g != null) {
							CMD.sendMessage(p, "§aThe group contains these players:");
							for (String s : getPlayers(g)) {
								CMD.sendMessage(p, "-" + s);
							}
						} else {
							CMD.sendMessage(p, "§cFailed to list the players of the group!");
						}
					} else if (args[0].equalsIgnoreCase("getgroups")) {
						if (ph.getPlayers().contains(args[1])) {
							CMD.sendMessage(p, "§aThe player §6" + args[1] + "§a is in these groups:");
							for (String s : ph.getGroups(args[1])) {
								CMD.sendMessage(p, "-" + s);
							}
						} else {
							CMD.sendMessage(p, "§cFailed to list the players groups!");
						}
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("add")) {
						if (gh.addPermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe permission §6" + args[2] + "§a was added to the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cAdding the permission failed!");
						}
					} else if (args[0].equalsIgnoreCase("remove")) {
						if (gh.removePermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + "§a was removed from the group §6"
									+ args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the permission failed!");
						}
					} else if (args[0].equalsIgnoreCase("addplayer")) {
						if (ph.addGroup(gh.getGroup(args[2]), args[1])) {
							CMD.sendMessage(p, "§aAdded §6" + args[1] + "§a to the group §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cAdding the player failed!");
						}
					} else if (args[0].equalsIgnoreCase("removeplayer")) {
						if (ph.removeGroup(gh.getGroup(args[2]), args[1])) {
							if(gh.getGroup(args[2]).isOp()) {
								boolean deopped = false;
								for(Player pl : Bukkit.getOnlinePlayers()) {
									if(pl.getName().equalsIgnoreCase(args[1])) {
										pl.setOp(false);
										deopped = true;
										break;
									}
								}
								if(!deopped) {
									for(OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
										if(pl.getName().equalsIgnoreCase(args[1])) {
											pl.setOp(false);
											deopped = true;
											break;
										}
									}
								}
							}
							CMD.sendMessage(p, "§aRemoved §6" + args[1] + "§a from the group §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the player failed!");
						}
					} else if (args[0].equalsIgnoreCase("setop")) {
						boolean op = Boolean.parseBoolean(args[2]);
						if (gh.setOp(gh.getGroup(args[1]), op)) {
							if (op) {
								CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was opped!");
							} else {
								CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deopped!");
							}
						} else {
							CMD.sendMessage(p, "§cFailed to op/deop the group!");
						}
					} else if (args[0].equalsIgnoreCase("setprefix")) {
						if (gh.setPrefix(gh.getGroup(args[1]), args[2].replace('&', '§'))) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a was set to §r"
									+ gh.getGroup(args[1]).getPrefix());
						} else {
							CMD.sendMessage(p, "§cFailed to set the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("setsuffix")) {
						if (gh.setSuffix(gh.getGroup(args[1]), args[2].replace('&', '§'))) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a was set to §r"
									+ gh.getGroup(args[1]).getSuffix());
						} else {
							CMD.sendMessage(p, "§cFailed to set the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1], gh.getGroup(args[2]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
							main.getConfigs().updateGroups();
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if(args[0].equalsIgnoreCase("negate")) {
						if(gh.negatePermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe negated permission §6" + args[2] + "§a was added to the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cNegating the permission failed!");
						}
					} else if(args[0].equalsIgnoreCase("removenegation")) {
						if(gh.removeNegatedPermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe negated permission §6" + args[2] + "§a was removed from the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the negated permission failed!");
						}
					} else if(args[0].equalsIgnoreCase("setrank")) {
						if(gh.setRank(gh.getGroup(args[1]), Integer.parseInt(args[2]))) {
							CMD.sendMessage(p, "§aSet the rank of the group §6" + args[1] + " §ato §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cCouldn't set the rank!");
						}
					}
				}
			}
		} else if (sender instanceof BlockCommandSender) {
			BlockCommandSender p = (BlockCommandSender) sender;

			if (command.getName().equalsIgnoreCase("group")) {
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1])) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
							main.getConfigs().updateGroups();
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if (args[0].equalsIgnoreCase("delete")) {
						if (gh.deleteGroup(gh.getGroup(args[0]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cThe group doesn't exists!");
						}
					} else if (args[0].equalsIgnoreCase("setdefault")) {
						if (gh.setDefault(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was set as default group!");
						} else {
							CMD.sendMessage(p, "§cSetting the default group failed!");
						}
					} else if (args[0].equalsIgnoreCase("getprefix")) {
						String pre = gh.getPrefix(gh.getGroup(args[1]));
						if (pre != null) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a is: §r" + pre);
						} else {
							CMD.sendMessage(p, "§cFailed to get the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("getsuffix")) {
						String suf = gh.getSuffix(gh.getGroup(args[1]));
						if (suf != null) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a is: §r" + suf);
						} else {
							CMD.sendMessage(p, "§cFailed to get the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("deleteprefix")) {
						if (gh.deletePrefix(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cFailed deleting the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("deletesuffix")) {
						if (gh.deleteSuffix(gh.getGroup(args[1]))) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cFailed deleting the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("getplayers")) {
						Group g = gh.getGroup(args[1]);
						if (g != null) {
							CMD.sendMessage(p, "§aThe group contains these players:");
							for (String s : getPlayers(g)) {
								CMD.sendMessage(p, "-" + s);
							}
						} else {
							CMD.sendMessage(p, "§cFailed to list the players of the group!");
						}
					} else if (args[0].equalsIgnoreCase("getgroups")) {
						if (ph.getPlayers().contains(args[1])) {
							CMD.sendMessage(p, "§aThe player §6" + args[1] + "§a is in these groups:");
							for (String s : ph.getGroups(args[1])) {
								CMD.sendMessage(p, "-" + s);
							}
						} else {
							CMD.sendMessage(p, "§cFailed to list the players groups!");
						}
					}
				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("add")) {
						if (gh.addPermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe permission §6" + args[2] + "§a was added to the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cAdding the permission failed!");
						}
					} else if (args[0].equalsIgnoreCase("remove")) {
						if (gh.removePermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + "§a was removed from the group §6"
									+ args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the permission failed!");
						}
					} else if (args[0].equalsIgnoreCase("addplayer")) {
						if (ph.addGroup(gh.getGroup(args[2]), args[1])) {
							CMD.sendMessage(p, "§aAdded §6" + args[1] + "§a to the group §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cAdding the player failed!");
						}
					} else if (args[0].equalsIgnoreCase("removeplayer")) {
						if (ph.removeGroup(gh.getGroup(args[2]), args[1])) {
							if(gh.getGroup(args[2]).isOp()) {
								boolean deopped = false;
								for(Player pl : Bukkit.getOnlinePlayers()) {
									if(pl.getName().equalsIgnoreCase(args[1])) {
										pl.setOp(false);
										deopped = true;
										break;
									}
								}
								if(!deopped) {
									for(OfflinePlayer pl : Bukkit.getOfflinePlayers()) {
										if(pl.getName().equalsIgnoreCase(args[1])) {
											pl.setOp(false);
											deopped = true;
											break;
										}
									}
								}
							}
							CMD.sendMessage(p, "§aRemoved §6" + args[1] + "§a from the group §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the player failed!");
						}
					} else if (args[0].equalsIgnoreCase("setop")) {
						boolean op = Boolean.parseBoolean(args[2]);
						if (gh.setOp(gh.getGroup(args[1]), op)) {
							if (op) {
								CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was opped!");
							} else {
								CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deopped!");
							}
						} else {
							CMD.sendMessage(p, "§cFailed to op/deop the group!");
						}
					} else if (args[0].equalsIgnoreCase("setprefix")) {
						if (gh.setPrefix(gh.getGroup(args[1]), args[2].replace('&', '§'))) {
							CMD.sendMessage(p, "§aThe prefix of the group §6" + args[1] + "§a was set to §r"
									+ gh.getGroup(args[1]).getPrefix());
						} else {
							CMD.sendMessage(p, "§cFailed to set the prefix!");
						}
					} else if (args[0].equalsIgnoreCase("setsuffix")) {
						if (gh.setSuffix(gh.getGroup(args[1]), args[2].replace('&', '§'))) {
							CMD.sendMessage(p, "§aThe suffix of the group §6" + args[1] + "§a was set to §r"
									+ gh.getGroup(args[1]).getSuffix());
						} else {
							CMD.sendMessage(p, "§cFailed to set the suffix!");
						}
					} else if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1], gh.getGroup(args[2]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
							main.getConfigs().updateGroups();
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if(args[0].equalsIgnoreCase("negate")) {
						if(gh.negatePermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe negated permission §6" + args[2] + "§a was added to the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cNegating the permission failed!");
						}
					} else if(args[0].equalsIgnoreCase("removenegation")) {
						if(gh.removeNegatedPermission(gh.getGroup(args[1]), args[2])) {
							CMD.sendMessage(p,
									"§aThe negated permission §6" + args[2] + "§a was removed from the group §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cRemoving the negated permission failed!");
						}
					} else if(args[0].equalsIgnoreCase("setrank")) {
						if(gh.setRank(gh.getGroup(args[1]), Integer.parseInt(args[2]))) {
							CMD.sendMessage(p, "§aSet the rank of the group §6" + args[1] + " §ato §6" + args[2] + "§a!");
						} else {
							CMD.sendMessage(p, "§cCouldn't set the rank!");
						}
					}
				}
			}
		}
		
		main.reloadPlayers();
		
		return false;
	}

	private ArrayList<String> getPlayers(Group g) {
		ArrayList<String> players = new ArrayList<String>();
		for (String s : ph.getPlayers()) {
			if (ph.isInGroup(s, g)) {
				players.add(s);
			}
		}
		return players;
	}

}
