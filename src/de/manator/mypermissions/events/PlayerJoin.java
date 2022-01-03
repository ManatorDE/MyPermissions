package de.manator.mypermissions.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.manator.mypermissions.Main;

public class PlayerJoin implements Listener {
	
	private Main main;
	
	public PlayerJoin(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		main.getPlayerHandler().addPlayer(e.getPlayer().getName());
		main.reloadPlayers();
	}
	
}
