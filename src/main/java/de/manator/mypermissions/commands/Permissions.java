package de.manator.mypermissions.commands;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;

/**
 * The CommandExecutor of the permissions command
 * @author ManatorDE
 */
public class Permissions implements CommandExecutor {
	
	/**
	 * A reference to the PlayerHandler object of MyPermissions
	 */
	private PlayerHandler ph;
	
	/**
	 * A reference to the Main object of MyPermissions
	 */
	private Main main;
	
	/**
	 * The constructor of Permissions
	 * @param main A reference to the Main object of MyPermissions
	 */
	public Permissions(Main main) {
		this.main = main;
		this.ph = main.getPlayerHandler();
	}
	
	/**
	 * A method that gets called when a command was send
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(command.getName().equalsIgnoreCase("permissions")) {
				if(args.length == 3) {
					if(args[0].equalsIgnoreCase("add")) {
						if(ph.addPermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas given to the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be given to the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(ph.removePermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas removed from the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be removed to the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("negate")) {
						if(ph.negatePermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas negated for the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be negated for the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("add")) {
						if(ph.removeNegatedPermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §ais no longer negated for the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe negation for permission §6" + args[2] + " §ccouldn't be removed for the player §6" + args[1] + "§c!");
						}
					}
				}
			}
		} else if(sender instanceof ConsoleCommandSender) {
			ConsoleCommandSender p = (ConsoleCommandSender) sender;
			if(command.getName().equalsIgnoreCase("permissions")) {
				if(args.length == 3) {
					if(args[0].equalsIgnoreCase("add")) {
						if(ph.addPermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas given to the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be given to the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(ph.removePermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas removed from the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be removed to the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("negate")) {
						if(ph.negatePermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas negated for the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be negated for the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("add")) {
						if(ph.removeNegatedPermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §ais no longer negated for the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe negation for permission §6" + args[2] + " §ccouldn't be removed for the player §6" + args[1] + "§c!");
						}
					}
				}
			}
		} else if(sender instanceof BlockCommandSender) {
			BlockCommandSender p = (BlockCommandSender) sender;
			if(command.getName().equalsIgnoreCase("permissions")) {
				if(args.length == 3) {
					if(args[0].equalsIgnoreCase("add")) {
						if(ph.addPermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas given to the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be given to the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("remove")) {
						if(ph.removePermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas removed from the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be removed to the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("negate")) {
						if(ph.negatePermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §awas negated for the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe permission §6" + args[2] + " §ccouldn't be negated for the player §6" + args[1] + "§c!");
						}
					} else if(args[0].equalsIgnoreCase("add")) {
						if(ph.removeNegatedPermission(args[1], args[2])) {
							CMD.sendMessage(p, "§aThe permission §6" + args[2] + " §ais no longer negated for the player §6" + args[1] + "§a!");
						} else {
							CMD.sendMessage(p, "§cThe negation for permission §6" + args[2] + " §ccouldn't be removed for the player §6" + args[1] + "§c!");
						}
					}
				}
			}
		}
		
		main.reloadPlayers();
		return false;
	}

}
