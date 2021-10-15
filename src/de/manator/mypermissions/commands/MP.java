package de.manator.mypermissions.commands;

import java.util.ArrayList;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.manator.mypermissions.Main;

// ToDo's: reloadPlayers implementation

public class MP implements CommandExecutor {

	private Main main;
	private ArrayList<String> commands;

	public MP(Main main) {
		this.main = main;
		this.commands = main.getCommands();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("mp")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;

				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						reloadPlayers();
						CMD.sendMessage(p, "§aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, main.getCommand(s).getDescription());
							}
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, main.getCommand(s).getDescription());
							}
						}
					}
				}

			} else if(sender instanceof ConsoleCommandSender) {
				ConsoleCommandSender p = (ConsoleCommandSender) sender;
				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						reloadPlayers();
						CMD.sendMessage(p, "§aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, main.getCommand(s).getDescription());
							}
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, main.getCommand(s).getDescription());
							}
						}
					}
				}
			} else if(sender instanceof BlockCommandSender) {
				BlockCommandSender p = (BlockCommandSender) sender;
				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						reloadPlayers();
						CMD.sendMessage(p, "§aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, main.getCommand(s).getDescription());
							}
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, main.getCommand(s).getDescription());
							}
						}
					}
				}
			}
		}
		return false;
	}

	private void reloadPlayers() {
		//main.getGroupHandler().getGroup(name);
	}
}
