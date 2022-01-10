package de.manator.mypermissions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;

public class PermissionsTab implements TabCompleter {

	private PlayerHandler ph;

	public PermissionsTab(Main main) {
		this.ph = main.getPlayerHandler();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> list = new ArrayList<>();

		if (command.getName().equalsIgnoreCase("permissions")) {
			if (args.length == 1) {
				list.add("add");
				list.add("remove");
				list.add("negate");
				list.add("removenegation");
				list = cleanUp(list, args[0]);
			} else if (args.length == 2) {
				list = cleanUp(ph.getPlayers(), args[1]);
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("remove")) {
					list = cleanUp(ph.getPermissions(args[1]), args[2]);
				} else if(args[0].equalsIgnoreCase("removenegation")) {
					list = cleanUp(ph.getNegatedPermissions(args[1]), args[2]);
				}
			}
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
