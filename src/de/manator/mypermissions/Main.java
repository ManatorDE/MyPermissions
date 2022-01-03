package de.manator.mypermissions;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.manator.mypermissions.commands.GroupCMD;
import de.manator.mypermissions.commands.GroupTab;
import de.manator.mypermissions.commands.MP;
import de.manator.mypermissions.commands.MPTab;
import de.manator.mypermissions.commands.Permissions;
import de.manator.mypermissions.commands.PermissionsTab;
import de.manator.mypermissions.events.CommandPreprocess;
import de.manator.mypermissions.events.PlayerJoin;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;

public class Main extends JavaPlugin {

	private ArrayList<String> commands;
	private GroupHandler gh;
	private PlayerHandler ph;

	@Override
	public void onEnable() {
		getLogger().info("Loading files...");
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		getLogger().info("Files loaded!");

		getLogger().info("Registering listeners...");
		registerListeners();
		getLogger().info("Listeners registered!");

		getLogger().info("Loading groups...");
		gh = new GroupHandler(getDataFolder());
		if (gh.loadGroups()) {
			getLogger().info("Loaded groups!");
		} else {
			getLogger().info("Loading groups failed!");
		}

		getLogger().info("Loading players...");
		ph = new PlayerHandler(getDataFolder());
		getLogger().info("Players loeaded!");
		
		getLogger().info("Loading commands...");
		registerCommands();
		getLogger().info("Commands loaded!");
	}

	@Override
	public void onDisable() {

	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new CommandPreprocess(), this);
	}

	private void registerCommands() {
		commands = new ArrayList<String>();

		commands.add("mp");
		getCommand("mp").setExecutor(new MP(this));
		getCommand("mp").setTabCompleter(new MPTab());

		commands.add("group");
		getCommand("group").setExecutor(new GroupCMD(this));
		getCommand("group").setTabCompleter(new GroupTab(gh, ph));

		commands.add("permissions");
		getCommand("permissions").setExecutor(new Permissions(this));
		getCommand("permissions").setTabCompleter(new PermissionsTab());
	}

	public ArrayList<String> getCommands() {
		return commands;
	}

	public GroupHandler getGroupHandler() {
		return gh;
	}

	public PlayerHandler getPlayerHandler() {
		return ph;
	}
	
	public void reloadPlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(ph.getPlayers().contains(p.getName())) {
				Group prefix = null;
				for(String gr : ph.getGroups(p.getName())) {
					Group g = gh.getGroup(gr);
					if(g != null) {
						if(prefix == null || prefix.getRank() < g.getRank()) {
							prefix = g;
						}
						
						if(g.isOp()) {
							p.setOp(true);
						}
						for(String perm : gh.getPermissions(gh.getGroup(gr))) {
							p.addAttachment(this).setPermission(perm, true);
						}
					}
				}
				for(String perm : ph.getPermissions(p.getName())) {
					p.addAttachment(this).setPermission(perm, true);
				}
				if(prefix != null) {
					p.setCustomName(prefix.getPrefix() + " " + ChatColor.WHITE + p.getName() + " " + prefix.getSuffix());
					p.setDisplayName(prefix.getPrefix() + " " + ChatColor.WHITE + p.getName() + " " + prefix.getSuffix());
					p.setPlayerListName(prefix.getPrefix() + " " + ChatColor.WHITE + p.getName() + " " + prefix.getSuffix());
					p.setCustomNameVisible(true);
				}
			}
		}
	}

}
