package de.manator.mypermissions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.earth2me.essentials.Essentials;

import de.manator.mypermissions.commands.ExcludeFromDefaultCMD;
import de.manator.mypermissions.commands.ExcludeTab;
import de.manator.mypermissions.commands.GroupCMD;
import de.manator.mypermissions.commands.GroupTab;
import de.manator.mypermissions.commands.MP;
import de.manator.mypermissions.commands.MPTab;
import de.manator.mypermissions.commands.Permissions;
import de.manator.mypermissions.commands.PermissionsTab;
import de.manator.mypermissions.config.ConfigFile;
import de.manator.mypermissions.events.ConfigEvents;
import de.manator.mypermissions.events.PlayerJoin;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.io.FileHandler;
import de.manator.mypermissions.players.PlayerHandler;
import de.manator.mypermissions.players.PlayerUpdater;
import de.manator.mypermissions.web.HttpServer;

/**
 * The main class of MyPermissions
 * 
 * @author ManatorDE
 */
public class Main extends JavaPlugin {

	// Add permission by given command

	/**
	 * A list of all MyPermissions commands
	 */
	private LinkedList<String> commands;

	/**
	 * A reference to the GroupHandler of MyPermissions
	 */
	private GroupHandler gh;

	/**
	 * A reference to the PlayerHandler of MyPermissions
	 */
	private PlayerHandler ph;

	/**
	 * A map storing all permission nodes of all players
	 */
	private HashMap<UUID, PermissionAttachment> perms;

	/**
	 * A reference to the events used for group configuration
	 */
	private ConfigEvents configs;

	/**
	 * A reference to the config file of MyPermissions
	 */
	private ConfigFile configFile;
	
	/**
	 * A reference to the internal webserver
	 */
	private HttpServer server;

	/**
	 * A method called when the plugin gets enabled
	 */
	@Override
	public void onEnable() {
		getLogger().info("Initializing bStats...");
		int pluginID = 16698;
		Metrics m = new Metrics(this, pluginID);
		// Getting rid of the annoying warning
		m.toString();
		getLogger().info("Metrics initialized!");

		getLogger().info("Loading files...");
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		configFile = new ConfigFile(getDataFolder(), "config.yml");
		getLogger().info("Files loaded!");

		getLogger().info("Loading groups...");
		gh = new GroupHandler(getDataFolder());
		if (gh.loadGroups()) {
			getLogger().info("Loaded groups!");
		} else {
			getLogger().info("Loading groups failed!");
		}

		getLogger().info("Resetting scoreboard...");
		Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
		for (Group g : gh.getGroups()) {
			if (g != null && sb.getTeam(g.getName()) != null) {
				sb.getTeam(g.getName()).unregister();
			}
		}
		getLogger().info("Scoreboard reset!");

		getLogger().info("Loading players...");
		ph = new PlayerHandler(getDataFolder());
		getLogger().info("Players loaded!");

		getLogger().info("Loading commands...");
		registerCommands();
		getLogger().info("Commands loaded!");

		perms = new HashMap<>();

		getLogger().info("Registering listeners...");
		registerListeners();
		getLogger().info("Listeners registered!");
		
		getLogger().info("Loading web files...");
		initWeb();
		getLogger().info("Loaded web files!");
		
		getLogger().info("Initializing webserver...");
		if(server == null) {
			server = new HttpServer(configFile.getWebserverPort(), this);
			if(configFile.isWebServerEnabled()) {
			    server.runServer();
			}
		}
		getLogger().info("Webserver initialized!");
		
		essentialsFix();
	}

	/**
	 * A method that gets called when the plugin gets disabled
	 */
	@Override
	public void onDisable() {
		getLogger().info("Resetting scoreboard...");
		Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
		for (Group g : gh.getGroups()) {
			if (g != null && sb.getTeam(g.getName()) != null) {
				sb.getTeam(g.getName()).unregister();
			}
		}
		getLogger().info("Scoreboard reset!");
		if(configFile.isWebServerEnabled()) {
		    server.stopServer();
		}
	}

	/**
	 * A method that is used to register all listeners of MyPermissions
	 */
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);

		configs = new ConfigEvents(this);
		Bukkit.getPluginManager().registerEvents(configs, this);
	}

	/**
	 * A method that is used to register all commands of MyPermissions
	 */
	private void registerCommands() {
		commands = new LinkedList<String>();

		commands.add("mp");
		getCommand("mp").setExecutor(new MP(this));
		getCommand("mp").setTabCompleter(new MPTab());

		commands.add("group");
		getCommand("group").setExecutor(new GroupCMD(this));
		getCommand("group").setTabCompleter(new GroupTab(this));

		commands.add("permissions");
		getCommand("permissions").setExecutor(new Permissions(this));
		getCommand("permissions").setTabCompleter(new PermissionsTab(this));

		commands.add("excludefromdefault");
		getCommand("excludefromdefault").setExecutor(new ExcludeFromDefaultCMD(this));
		getCommand("excludefromdefault").setTabCompleter(new ExcludeTab(this));
	}

	/**
	 * A method used to enable the essentials fix
	 */
	private void essentialsFix() {
		if (configFile.getEssentialsDisplayNameDisabled()) {
			getLogger().info("Checking for essentials...");
			Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			if (ess != null) {
				if (ess.isEnabled()) {
					getLogger().info("Essentials found...");
					ess.getConfig().set("change-displayname", false);
					try {
						ess.getConfig().save(new File(ess.getDataFolder() + "/config.yml"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					getLogger().info("Essentials custom nickname disabled!");
				} else {
					Bukkit.getScheduler().runTaskLater(this, new Runnable() {

						@Override
						public void run() {
							getLogger().info("Essentials found...");
							ess.getConfig().set("change-displayname", false);
							try {
								ess.getConfig().save(new File(ess.getDataFolder() + "/config.yml"));
							} catch (IOException e) {
								e.printStackTrace();
							}
							ess.reloadConfig();
							getLogger().info("Essentials custom nickname disabled!");
						}
					}, 5000);
				}
				ess.reloadConfig();
			} else {
				getLogger().info("Essentials not found!");
			}
		}
	}

	/**
	 * Gets a list of all MyPermissions commands
	 * 
	 * @return LinkedList with all MyPermissions commands as Strings
	 */
	public LinkedList<String> getCommands() {
		return commands;
	}

	/**
	 * Gets the reference to the GroupHandler of MyPermissions
	 * 
	 * @return A reference to the GroupHandler
	 */
	public GroupHandler getGroupHandler() {
		return gh;
	}

	/**
	 * Gets the reference to the PlayerHandler of MyPermissions
	 * 
	 * @return A reference to the PlayerHandler
	 */
	public PlayerHandler getPlayerHandler() {
		return ph;
	}

	/**
	 * A method used to reload the player data - Permissions - Grouos - Prefixes and
	 * suffixes
	 */
	public void reloadPlayers() {
		Bukkit.getScheduler().runTaskAsynchronously(this, new PlayerUpdater(this));

		for (Player pl : Bukkit.getOnlinePlayers()) {
			pl.updateCommands();
		}
	}

	/**
	 * A method used to reload the player data of a single player
	 * 
	 * @param p The player that should be reloaded
	 */
	public void reloadPlayer(Player p) {
		PermissionAttachment attachment = getPerms().get(p.getUniqueId());
		if (attachment == null) {
			attachment = p.addAttachment(this);
			getPerms().put(p.getUniqueId(), attachment);
		} else {
			attachment = p.addAttachment(this);
			getPerms().replace(p.getUniqueId(), attachment);
		}
		if (ph.getPlayers().contains(p.getName())) {
			Group prefix = null;
			for (String gr : ph.getGroups(p.getName())) {
				Group g = gh.getGroup(gr);
				if (g != null) {
					if (prefix == null || prefix.getRank() < g.getRank()) {
						prefix = g;
					}
					if (g.isOp()) {
						p.setOp(true);
					}
					for (String perm : gh.getPermissions(gh.getGroup(gr))) {
						attachment.setPermission(perm, true);
					}
					for (String nperm : gh.getNegatedPermissions(gh.getGroup(gr))) {
						attachment.setPermission(nperm, false);
					}
				}
			}

			for (String perm : ph.getPermissions(p.getName())) {
				attachment.setPermission(perm, true);
			}
			for (String nperm : ph.getNegatedPermissions(p.getName())) {
				attachment.setPermission(nperm, false);
			}
			String name = "";
			Team t = null;
			if (prefix != null) {
				Scoreboard s = Bukkit.getScoreboardManager().getMainScoreboard();

				if (s.getTeam(prefix.getName()) == null) {
					t = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(prefix.getName());
				} else {
					t = s.getTeam(prefix.getName());
				}
				if (prefix.getPrefix() != null) {
					t.setPrefix(prefix.getPrefix());
					name += prefix.getPrefix();
				}
				name += ChatColor.WHITE + p.getName();
				if (prefix.getSuffix() != null) {
					t.setPrefix(prefix.getSuffix());
					name += prefix.getSuffix();
				}
			}

			if (t != null) {
				t.addEntry(p.getName());
				p.setCustomName(name);
				p.setDisplayName(name);
				p.setPlayerListName(name);
				p.setCustomNameVisible(true);
			}
			p.updateCommands();
		}
	}

	/**
	 * Downloads the latest website version of the Config Panel
	 */
	private void initWeb() {
		File dest = new File(this.getDataFolder().getAbsolutePath() + "/web");
		if(!dest.exists()) {
			dest.mkdir();
		} else {
			FileHandler.removeRecursive(dest);
			dest.mkdir();
		}
		
		FileHandler.unzipFile("https://dev.manator.de/dl/mp/web-latest.zip", dest);
	}

	/**
	 * Gets a reference to MyPermissions group config events
	 * 
	 * @return A reference to the ConfigEvents
	 */
	public ConfigEvents getConfigs() {
		return configs;
	}

	/**
	 * Gets the perms HashMap
	 * 
	 * @return A HashMap of all permissions of all player
	 */
	public HashMap<UUID, PermissionAttachment> getPerms() {
		return perms;
	}

	/**
	 * Gets the config file of my permissions
	 * 
	 * @return A reference to the ConfigFile object of MyPermissions
	 */
	public ConfigFile getConfigFile() {
		return configFile;
	}
}
