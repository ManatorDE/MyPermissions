package de.manator.mypermissions.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.manator.mypermissions.Main;

public class Permissions implements CommandExecutor {
	
	private Main main;
	
	public Permissions(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		return false;
	}

}
