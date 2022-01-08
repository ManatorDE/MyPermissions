package de.manator.mypermissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;

public class ExcludeFromDefaultCMD implements CommandExecutor {
	
	private Main main;
	private PlayerHandler ph;

	public ExcludeFromDefaultCMD(Main main) {
		this.main = main;
		ph = this.main.getPlayerHandler();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("excludefromdefault")) {
			if(args.length == 2) {
				if(ph.excludeFromDefault(args[0], Boolean.parseBoolean(args[1]))) {
					CMD.sendMessage(sender, "§aThe player §6" + args[0] + " §awas excluded from the default group!");
				} else {
					CMD.sendMessage(sender, "§cPlayer couldn't be exluded from the default group!");
				}
			}
		}
		
		return false;
	}

}
