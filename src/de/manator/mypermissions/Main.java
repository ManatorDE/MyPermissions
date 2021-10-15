package de.manator.mypermissions;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.manator.mypermissions.commands.GroupCMD;
import de.manator.mypermissions.commands.GroupTab;
import de.manator.mypermissions.commands.MP;
import de.manator.mypermissions.commands.MPTab;
import de.manator.mypermissions.commands.Permissions;
import de.manator.mypermissions.commands.PermissionsTab;
import de.manator.mypermissions.events.PlayerJoin;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;

public class Main extends JavaPlugin {
	
	private ArrayList<String> commands;
	private GroupHandler gh;
	private PlayerHandler ph;
	
	@Override
	public void onEnable() {
		getLogger().info("Loading files...");
		if(!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		getLogger().info("Files loaded!");
		
		getLogger().info("Loading commands...");
		registerCommands();
		getLogger().info("Commands loaded!");
		
		getLogger().info("Registering listeners...");
		registerListeners();
		getLogger().info("Listeners registered!");
		
		getLogger().info("Loading groups...");
		gh = new GroupHandler(getDataFolder());
		if(gh.loadGroups()) {
			getLogger().info("Loaded groups!");
		} else {
			getLogger().info("Loading groups failed!");
		}
		
		getLogger().info("Loading players...");
		ph = new PlayerHandler(getDataFolder());
	}

	@Override
	public void onDisable() {
		
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
	}
	
	private void registerCommands() {
		commands = new ArrayList<String>();
		
		commands.add("mp");
		getCommand("mp").setExecutor(new MP(this));
		getCommand("mp").setTabCompleter(new MPTab());
		
		commands.add("group");
		getCommand("group").setExecutor(new GroupCMD(this));
		getCommand("group").setTabCompleter(new GroupTab());
		
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
	
}
