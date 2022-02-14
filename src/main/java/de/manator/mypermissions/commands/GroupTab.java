package de.manator.mypermissions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;

public class GroupTab implements TabCompleter {

	private GroupHandler gh;
	private PlayerHandler ph;

	public GroupTab(Main main) {
		this.gh = main.getGroupHandler();
		this.ph = main.getPlayerHandler();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (args.length == 1) {
			list.add("add");
			list.add("addplayer");
			list.add("create");
			list.add("delete");
			list.add("deleteprefix");
			list.add("deletesuffix");
			list.add("getprefix");
			list.add("getsuffix");
			list.add("getdefault");
			list.add("getplayers");
			list.add("getgroups");
			list.add("negate");
			list.add("removenegation");
			list.add("remove");
			list.add("removeplayer");
			list.add("setprefix");
			list.add("setsuffix");
			list.add("setdefault");
			list.add("setop");
			list.add("setrank");
			list = cleanUp(list, args[0]);
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
				for (Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if (args[0].equalsIgnoreCase("addplayer") || args[0].equalsIgnoreCase("removeplayer")) {
				list = ph.getPlayers();
			} else if (args[0].equalsIgnoreCase("create")) {
				list.add("<groupname>");
			} else if (args[0].equalsIgnoreCase("delete")) {
				for (Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if (args[0].equalsIgnoreCase("deleteprefix") || args[0].equalsIgnoreCase("deletesuffix")) {
				for (Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if (args[0].equalsIgnoreCase("getprefix") || args[0].equalsIgnoreCase("getsuffix")
					|| args[0].equalsIgnoreCase("getplayers")) {
				for (Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if (args[0].equalsIgnoreCase("getgroups")) {
				list = ph.getPlayers();
			} else if (args[0].equalsIgnoreCase("setprefix") || args[0].equalsIgnoreCase("setsufix")
					|| args[0].equalsIgnoreCase("setdefault")) {
				for (Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if (args[0].equalsIgnoreCase("setop")) {
				for (Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if(args[0].equalsIgnoreCase("removenegation")) {
				for(Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if(args[0].equalsIgnoreCase("setrank")) {
				for(Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			} else if(args[0].equalsIgnoreCase("negate")) {
				for(Group g : gh.getGroups()) {
					if(g != null) {
						list.add(g.getName());
					}
				}
			}
			list = cleanUp(list, args[1]);
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("addplayer")) {
				for (Group g : gh.getGroups()) {
					if (!ph.isInGroup(args[1], g)) {
						if(g != null) {
							list.add(g.getName());
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("removeplayer")) {
				for (Group g : gh.getGroups()) {
					if (ph.isInGroup(args[1], g)) {
						if(g != null) {
							list.add(g.getName());
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				list = gh.getPermissions(gh.getGroup(args[1]));
			} else if (args[0].equalsIgnoreCase("create")) {
				for (Group g : gh.getGroups()) {
					list.add(g.getName());
				}
			} else if (args[0].equalsIgnoreCase("setprefix")) {
				list.add("<prefix>");
			} else if (args[0].equalsIgnoreCase("setsufix")) {
				list.add("<suffix>");
			} else if (args[0].equalsIgnoreCase("setop")) {
				list.add("true");
				list.add("false");
			} else if(args[0].equalsIgnoreCase("removenegation")) {
				list = gh.getNegatedPermissions(gh.getGroup(args[1]));
			}
			list = cleanUp(list, args[2]);
		}
		return list;
	}
	
	private ArrayList<String> cleanUp(ArrayList<String> list, String arg) {
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).startsWith(arg)) {
				list.remove(i);
			}
		}
		return list;
	}
	
}
