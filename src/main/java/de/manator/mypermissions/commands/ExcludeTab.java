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
 * The TabCompleter of the excludefromdefault command
 * @author ManatorDE
 */
public class ExcludeTab implements TabCompleter {
	
	/**
	 * A reference to the PlayerHandler of MyPermissions
	 */
	private final PlayerHandler ph;
	
	/**
	 * The constructor of ExludeTab
	 * @param main A reference to the Main object of MyPermissions
	 */
	public ExcludeTab(Main main) {
		this.ph = main.getPlayerHandler();
	}
	
	/**
	 * A method used to get a list of possible tab completions for the ExludeFromDefaultCMD
	 */
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
		LinkedList<String> list = new LinkedList<>();
		
		if(cmd.getName().equalsIgnoreCase("excludefromdefault")) {
			if(args.length == 1) {
				list = ph.getPlayers();
                list.removeIf(item -> !item.startsWith(args[0]));
			} else if(args.length == 2) {
				list.add("true");
				list.add("false");
			}
		}
		
		return list;
	}

}
