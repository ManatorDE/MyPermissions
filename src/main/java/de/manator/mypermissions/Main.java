package de.manator.mypermissions;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.manator.mypermissions.commands.ExcludeFromDefaultCMD;
import de.manator.mypermissions.commands.ExcludeTab;
import de.manator.mypermissions.commands.GroupCMD;
import de.manator.mypermissions.commands.GroupTab;
import de.manator.mypermissions.commands.MP;
import de.manator.mypermissions.commands.MPTab;
import de.manator.mypermissions.commands.Permissions;
import de.manator.mypermissions.commands.PermissionsTab;
import de.manator.mypermissions.events.ConfigEvents;
import de.manator.mypermissions.events.PlayerJoin;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;

public class Main extends JavaPlugin {

	// Add permission by given command

	private LinkedList<String> commands;
	private GroupHandler gh;
	private PlayerHandler ph;
	private HashMap<UUID, PermissionAttachment> perms;
	private ConfigEvents configs;

	@Override
	public void onEnable() {
		getLogger().info("Loading files...");
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
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
	}

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
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		
		configs = new ConfigEvents(this);
		Bukkit.getPluginManager().registerEvents(configs, this);
	}

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

	public LinkedList<String> getCommands() {
		return commands;
	}

	public GroupHandler getGroupHandler() {
		return gh;
	}

	public PlayerHandler getPlayerHandler() {
		return ph;
	}

	public void reloadPlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PermissionAttachment attachment = perms.get(p.getUniqueId());
			if (attachment == null) {
				attachment = p.addAttachment(this);
				perms.put(p.getUniqueId(), attachment);
			} else {
				attachment = p.addAttachment(this);
				perms.replace(p.getUniqueId(), attachment);
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
	}

	public void reloadPlayer(Player p) {
		PermissionAttachment attachment = perms.get(p.getUniqueId());
		if (attachment == null) {
			attachment = p.addAttachment(this);
			perms.put(p.getUniqueId(), attachment);
		} else {
			attachment = p.addAttachment(this);
			perms.replace(p.getUniqueId(), attachment);
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
	
	public ConfigEvents getConfigs() {
		return configs;
	}
}
