package de.manator.mypermissions.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;
import org.jetbrains.annotations.NotNull;

/**
 * The CommandExecutor of the excludefromdefault command
 * @author ManatorDE
 */
public class ExcludeFromDefaultCMD implements CommandExecutor {
	
	/**
	 * A reference to the main object of MyPwrmissions
	 */
	private Main main;
	
	/**
	 * A reference to the PlayerHandler of MyPermissions
	 */
	private PlayerHandler ph;

	/**
	 * The constructor of ExcludeFromDefaultCMD
	 * @param main A reference to the Main object
	 */
	public ExcludeFromDefaultCMD(Main main) {
		this.main = main;
		ph = this.main.getPlayerHandler();
	}
	
	/**
	 * A method called, when a command was send
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("excludefromdefault")) {
			if(args.length == 2) {
				if(ph.excludeFromDefault(args[0], Boolean.parseBoolean(args[1]))) {
					if(Boolean.parseBoolean(args[1])) {
                        CMD.sendMessage(sender, ChatColor.GREEN + "The player " + ChatColor.GOLD + args[0] + ChatColor.GREEN + " was excluded from the default group!");
					} else {
						CMD.sendMessage(sender, ChatColor.GREEN + "The player " + ChatColor.GOLD + args[0] + ChatColor.GREEN + " can join the default group now!");
					}
				} else {
					CMD.sendMessage(sender, ChatColor.RED + "Player couldn't be exluded from the default group!");
				}
			}
		}
		
		main.reloadPlayers();
		return false;
	}

}
