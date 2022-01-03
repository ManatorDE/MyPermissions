package de.manator.mypermissions.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import de.manator.mypermissions.commands.CMD;

public class CommandPreprocess implements Listener {
	
	@EventHandler
	public void commandPreprecess(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage();
		String[] cmd = command.split("\\s+");
		
		if(cmd.length == 2) {
			if(cmd[0].equalsIgnoreCase("op")) {
				if(e.getPlayer().hasPermission("minecraft.command.op")) {
					Player p = Bukkit.getPlayer(cmd[1]);
					if(p.isOp()) {
						CMD.sendMessage(e.getPlayer(), "§cThe player is already opped!");
					} else {
						p.setOp(true);
						CMD.sendMessage(p, "§aYou were opped!");
						CMD.sendMessage(e.getPlayer(), "§aThe player §6" + cmd[1] + "§a was opped!");
					}
				}
			} else if(cmd[0].equalsIgnoreCase("deop")) {
				if(e.getPlayer().hasPermission("minecraft.command.deop")) {
					Player p = Bukkit.getPlayer(cmd[1]);
					if(p.isOp()) {
						p.setOp(false);
						CMD.sendMessage(p, "§aYou were deopped!");
						CMD.sendMessage(e.getPlayer(), "§aThe player §6" + cmd[1] + "§a was deopped!");
					} else {
						CMD.sendMessage(e.getPlayer(), "§cThe player wasn't opped!");
					}
				}
			}
		}
	}
}
