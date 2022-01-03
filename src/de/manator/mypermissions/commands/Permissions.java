package de.manator.mypermissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;

public class Permissions implements CommandExecutor {
	
	private PlayerHandler ph;
	
	public Permissions(Main main) {
		this.ph = main.getPlayerHandler();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		return false;
	}

}
