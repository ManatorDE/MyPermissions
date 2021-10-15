package de.manator.mypermissions.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MPTab implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		if (command.getName().equalsIgnoreCase("mp")) {
			list = new ArrayList<String>();
			if (args.length == 1) {
				if(args[0].toLowerCase().startsWith("r")) {
					list.add("rl");
				} else if(args[0].toLowerCase().startsWith("h")) {
					list.add("help");
				} else {
					list.add("rl");
					list.add("help");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("help")) {
					list.add("1");
					list.add("2");
					list.add("3");
					list.add("4");
				}
			}
		}
		return list;
	}

}
