package de.manator.mypermissions.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import de.manator.mypermissions.Main;

/**
 * A class used to handle the PlayerJoinEvent and the PlayerLoginEvent
 * @author ManatorDE
 */
public class PlayerJoin implements Listener {

	/**
	 * A reference to the main object of MyPermissions
	 */
	private Main main;

	/**
	 * The constructor of PlayerJoin
	 * @param main A reference to the main object of MyPermissions
	 */
	public PlayerJoin(Main main) {
		this.main = main;
	}

	/**
	 * A method that gets called, when a player joins the server
	 * @param e The event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (main.getPlayerHandler().addPlayer(e.getPlayer().getName())) {
			main.getPlayerHandler().addGroup(main.getGroupHandler().getDefault(), e.getPlayer().getName());
		}
		if (!main.getPlayerHandler().isInGroup(e.getPlayer().getName(), main.getGroupHandler().getDefault())
				&& !main.getPlayerHandler().isExcluded(e.getPlayer().getName())) {
			main.getPlayerHandler().addGroup(main.getGroupHandler().getDefault(), e.getPlayer().getName());
		}
		main.reloadPlayer(e.getPlayer());
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.updateCommands();
		}
	}
	
	/**
	 * A method that gets called, when a player logs in to the server
	 * @param e The event
	 */
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		if (main.getPlayerHandler().addPlayer(e.getPlayer().getName())) {
			main.getPlayerHandler().addGroup(main.getGroupHandler().getDefault(), e.getPlayer().getName());
		}
		if (!main.getPlayerHandler().isInGroup(e.getPlayer().getName(), main.getGroupHandler().getDefault())
				&& !main.getPlayerHandler().isExcluded(e.getPlayer().getName())) {
			main.getPlayerHandler().addGroup(main.getGroupHandler().getDefault(), e.getPlayer().getName());
		}
		main.reloadPlayer(e.getPlayer());
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.updateCommands();
		}
	}

}
