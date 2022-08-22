package de.manator.mypermissions.commands;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * A class used to send uniform messages to a CommandSender
 * @author ManatorDE
 */
public class CMD {
	/**
	 * Sends a message to a CommandSender
	 * @param p The CommandSender
	 * @param message The message
	 */
	public static void sendMessage(CommandSender p, String message) {
		if (p instanceof Player) {
			((Player) p).sendMessage("§b[§aMyPermissions§b]§r " + message);
		} else if (p instanceof ConsoleCommandSender) {
			((ConsoleCommandSender) p).sendMessage(message);
		} else if (p instanceof BlockCommandSender) {
			((BlockCommandSender) p).sendMessage(message);
		}
	}
}
