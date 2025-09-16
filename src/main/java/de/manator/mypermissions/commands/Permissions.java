package de.manator.mypermissions.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;
import org.jetbrains.annotations.NotNull;

/**
 * The CommandExecutor of the permissions command
 * @author ManatorDE
 */
public class Permissions implements CommandExecutor {
	
	/**
	 * A reference to the PlayerHandler object of MyPermissions
	 */
	private final PlayerHandler ph;
	
	/**
	 * A reference to the Main object of MyPermissions
	 */
	private final Main main;
	
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
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        switch (sender) {
            case Player p -> {
                if (command.getName().equalsIgnoreCase("permissions")) {
                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (ph.addPermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was given to the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be given to the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (ph.removePermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be removed to the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("negate")) {
                            if (ph.negatePermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was negated for the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be negated for the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("removenegation")) {
                            if (ph.removeNegatedPermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " is no longer negated for the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The negation for permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be removed for the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        }
                    }
                }
            }
            case ConsoleCommandSender p -> {
                if (command.getName().equalsIgnoreCase("permissions")) {
                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (ph.addPermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was given to the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be given to the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (ph.removePermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be removed to the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("negate")) {
                            if (ph.negatePermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was negated for the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be negated for the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("removenegation")) {
                            if (ph.removeNegatedPermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " is no longer negated for the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The negation for permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be removed for the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        }
                    }
                }
            }
            case BlockCommandSender p -> {
                if (command.getName().equalsIgnoreCase("permissions")) {
                    if (args.length == 3) {
                        if (args[0].equalsIgnoreCase("add")) {
                            if (ph.addPermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was given to the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be given to the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (ph.removePermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was removed from the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be removed to the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("negate")) {
                            if (ph.negatePermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.GREEN + " was negated for the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be negated for the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        } else if (args[0].equalsIgnoreCase("removenegation")) {
                            if (ph.removeNegatedPermission(args[1], args[2])) {
                                CMD.sendMessage(p, ChatColor.GREEN + "The permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " is no longer negated for the player " + ChatColor.GOLD + args[1] + ChatColor.GREEN + "!");
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The negation for permission " + ChatColor.GOLD + args[2] + ChatColor.RED + " couldn't be removed for the player " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
                            }
                        }
                    }
                }
            }
            default -> {
            }
        }
		
		main.reloadPlayers();
		return false;
	}

}
