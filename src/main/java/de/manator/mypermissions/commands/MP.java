package de.manator.mypermissions.commands;

import java.util.LinkedList;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.manator.mypermissions.Main;

/**
 * The CommandExecutor of the mp command
 * @author ManatorDE
 */
public class MP implements CommandExecutor {

	/**
	 * A reference to the Main object of MyPermissions
	 */
	private Main main;
	
	/**
	 * A list of all MyPermissions commands
	 */
	private LinkedList<String> commands;

	/**
	 * The constructor of MP
	 * @param main A reference to the Main object of MyPermissions
	 */
	public MP(Main main) {
		this.main = main;
		this.commands = main.getCommands();
	}

	/**
	 * A method that gets called when a command was send
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("mp")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;

				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						main.reloadPlayers();
						main.getGroupHandler().loadGroups();
						CMD.sendMessage(p, "§aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, "§6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "§6Visit https://manatorde.github.io/mypermissions/ for more information!");
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, "§6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "§6Visit https://manatorde.github.io/mypermissions/ for more information!");
						}
					}
				}

			} else if(sender instanceof ConsoleCommandSender) {
				ConsoleCommandSender p = (ConsoleCommandSender) sender;
				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						main.reloadPlayers();
						main.getGroupHandler().loadGroups();
						CMD.sendMessage(p, "§aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, "§6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "§6Visit https://manatorde.github.io/mypermissions/ for more information!");
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, "§6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "§6Visit https://manatorde.github.io/mypermissions/ for more information!");
						}
					}
				}
			} else if(sender instanceof BlockCommandSender) {
				BlockCommandSender p = (BlockCommandSender) sender;
				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						main.reloadPlayers();
						main.getGroupHandler().loadGroups();
						CMD.sendMessage(p, "§aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, "§6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "§6Visit https://manatorde.github.io/mypermissions/ for more information!");
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, "§6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "§6Visit https://manatorde.github.io/mypermissions/ for more information!");
						}
					}
				}
			}
		}
		
		main.reloadPlayers();
		return false;
	}
}
