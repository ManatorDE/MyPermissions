package de.manator.mypermissions.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;

/**
 * The TabCompleter of the permissions command
 * @author ManatorDE
 */
public class PermissionsTab implements TabCompleter {

	/**
	 * A reference to the PlayerHandler object of MyPermissions
	 */
	private PlayerHandler ph;

	/**
	 * The constructor of PermissionsTab
	 * @param main A reference to the Main object of MyPermissions
	 */
	public PermissionsTab(Main main) {
		this.ph = main.getPlayerHandler();
	}
	
	/**
	 * A method used to get a list of possible tab completions for the Permissions
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		LinkedList<String> list = new LinkedList<>();

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

	private LinkedList<String> cleanUp(LinkedList<String> list, String arg) {
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).startsWith(arg)) {
				list.remove(i);
			}
		}
		return list;
	}

}
