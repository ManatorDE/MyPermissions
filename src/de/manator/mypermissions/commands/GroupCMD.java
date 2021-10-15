package de.manator.mypermissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.groups.GroupHandler;

public class GroupCMD implements CommandExecutor {

	private Main main;
	private GroupHandler gh;
	
	public GroupCMD(Main main) {
		this.main = main;
		gh = this.main.getGroupHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (command.getName().equalsIgnoreCase("group")) {
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("create")) {
						if (gh.addGroup(args[1])) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was created!");
						} else {
							CMD.sendMessage(p, "§cThe group already exists!");
						}
					} else if(args[0].equalsIgnoreCase("delete")) {
						if(gh.deleteGroup(gh.getGroup(args[0]))) {
							CMD.sendMessage(p, "§aThe group §6" + args[1] + "§a was deleted!");
						} else {
							CMD.sendMessage(p, "§cThe group doesn't exists!");
						}
					} else if(args[0].equalsIgnoreCase("setdefault")) {
						
					}
				}
			}

		}

		return false;
	}

}
