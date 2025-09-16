package de.manator.mypermissions.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;
import org.jetbrains.annotations.NotNull;

/**
 * The TabCompleter of the permissions command
 * @author ManatorDE
 */
public class PermissionsTab implements TabCompleter {

	/**
	 * A reference to the PlayerHandler object of MyPermissions
	 */
	private final PlayerHandler ph;

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
	public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
		LinkedList<String> list = new LinkedList<>();

		if (command.getName().equalsIgnoreCase("permissions")) {
			if (args.length == 1) {
				list.add("add");
				list.add("remove");
				list.add("negate");
				list.add("removenegation");
                list.removeIf(item -> !item.startsWith(args[0]));
			} else if (args.length == 2) {
                list = ph.getPlayers();
                list.removeIf(item -> !item.startsWith(args[1]));
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("remove")) {
                    list = ph.getPermissions((args[1]));
                    list.removeIf(item -> !item.startsWith(args[2]));
				} else if(args[0].equalsIgnoreCase("removenegation")) {
                    list = ph.getNegatedPermissions((args[1]));
                    list.removeIf(item -> !item.startsWith(args[2]));
				}
			}
		}
		return list;
	}
}
