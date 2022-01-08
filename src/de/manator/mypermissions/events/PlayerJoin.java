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
		if (main.getPlayerHandler().addPlayer(e.getPlayer().getName())) {
			main.getPlayerHandler().addGroup(main.getGroupHandler().getDefault(), e.getPlayer().getName());
		}
		if (!main.getPlayerHandler().isInGroup(e.getPlayer().getName(), main.getGroupHandler().getDefault())
				&& !main.getPlayerHandler().isExcluded(e.getPlayer().getName())) {
			main.getPlayerHandler().addGroup(main.getGroupHandler().getDefault(), e.getPlayer().getName());
		}

		main.reloadPlayers();
	}

}
