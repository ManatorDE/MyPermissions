package de.manator.mypermissions.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * The TabCompleter of the mp command
 * @author ManatorDE
 */
public class MPTab implements TabCompleter {
	
	/**
	 * A method used to get a list of possible tab completions for the MP
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		LinkedList<String> list = new LinkedList<String>();
		if (command.getName().equalsIgnoreCase("mp")) {
			list = new LinkedList<String>();
			if (args.length == 1) {
				if(args[0].toLowerCase().startsWith("r")) {
					list.add("rl");
				} else if(args[0].toLowerCase().startsWith("h")) {
					list.add("help");
				} else {
					list.add("rl");
					list.add("help");
					list.add("enableEssentialsFix");
					list.add("disableEssentialsFix");
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
