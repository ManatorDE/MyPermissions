package de.manator.mypermissions.events;

import java.util.Arrays;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.commands.CMD;
import de.manator.mypermissions.config.GroupConfig;
import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.groups.GroupHandler;
import de.manator.mypermissions.players.PlayerHandler;
import net.md_5.bungee.api.ChatColor;

public class ConfigEvents implements Listener {

	private Main main;
	private GroupHandler gh;
	private PlayerHandler ph;
	private LinkedList<GroupConfig> configs;

	private Location loc;
	private Group g;
	private int type;

	public ConfigEvents(Main plugin) {
		main = plugin;
		gh = main.getGroupHandler();
		ph = main.getPlayerHandler();
		configs = new LinkedList<>();
		for (Group g : main.getGroupHandler().getGroups()) {
			if (g != null) {
				configs.add(new GroupConfig(g));
			}
		}
	}

	@EventHandler
	public void inventoryClicked(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		for (GroupConfig cfg : configs) {
			if (e.getInventory().equals(cfg.getInv())) {

				if (item != null) {
					if (item.equals(cfg.getConfig(GroupConfig.PREFIX))) {

						Block b = p.getLocation().getBlock();
						b.setType(Material.OAK_SIGN);

						Sign s = (Sign) b.getState();
						s.setEditable(true);

						p.openSign(s);
						loc = s.getLocation();
						g = cfg.getGroup();
						type = GroupConfig.PREFIX;
					} else if (item.equals(cfg.getConfig(GroupConfig.SUFFIX))) {

						Block b = p.getLocation().getBlock();
						b.setType(Material.OAK_SIGN);

						Sign s = (Sign) b.getState();
						s.setEditable(true);

						p.openSign(s);
						loc = s.getLocation();
						g = cfg.getGroup();
						type = GroupConfig.SUFFIX;
					} else if (item.equals(cfg.getConfig(GroupConfig.ADD_PLAYER))) {
						LinkedList<String> players = ph.getPlayers();
						players.removeAll(getPlayers(cfg.getGroup()));
						cfg.setMenu(players, GroupConfig.ADD_PLAYER);
						p.openInventory(cfg.getMenu());
					} else if (item.equals(cfg.getConfig(GroupConfig.REMOVE_PLAYER))) {

						cfg.setMenu(getPlayers(cfg.getGroup()), GroupConfig.REMOVE_PLAYER);
						p.openInventory(cfg.getMenu());

					} else if (item.equals(cfg.getConfig(GroupConfig.ADD_PERMISSION))) {
						LinkedList<String> permissions = new LinkedList<>();
						for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
							PluginDescriptionFile file = pl.getDescription();
							for (String command : file.getCommands().keySet()) {
								String perm = (String) file.getCommands().get(command).get("permission");
								if (perm != null) {
									if (!permissions.contains(perm)
											&& !gh.getPermissions(cfg.getGroup()).contains(perm)) {
										permissions.add(perm);
									}
								}
							}
							for (Permission perms : file.getPermissions()) {
								String perm = perms.getName();
								if (perm != null) {
									if (!permissions.contains(perm)
											&& !gh.getPermissions(cfg.getGroup()).contains(perm)) {
										permissions.add(perm);
									}
								}
							}
						}
						cfg.setMenu(permissions, GroupConfig.ADD_PERMISSION);
						p.openInventory(cfg.getMenu());

					} else if (item.equals(cfg.getConfig(GroupConfig.REMOVE_PERMISSION))) {

						cfg.setMenu(gh.getPermissions(cfg.getGroup()), GroupConfig.REMOVE_PERMISSION);
						p.openInventory(cfg.getMenu());

					} else if (item.equals(cfg.getConfig(GroupConfig.NEGATE))) {

						LinkedList<String> permissions = new LinkedList<>();
						for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
							PluginDescriptionFile file = pl.getDescription();
							for (String command : file.getCommands().keySet()) {
								String perm = (String) file.getCommands().get(command).get("permission");
								if (perm != null) {
									if (!permissions.contains(perm)
											&& !gh.getNegatedPermissions(cfg.getGroup()).contains(perm)) {
										permissions.add(perm);
									}
								}
							}
							for (Permission perms : file.getPermissions()) {
								String perm = perms.getName();
								if (perm != null) {
									if (!permissions.contains(perm)
											&& !gh.getNegatedPermissions(cfg.getGroup()).contains(perm)) {
										permissions.add(perm);
									}
								}
							}
						}
						cfg.setMenu(permissions, GroupConfig.NEGATE);
						p.openInventory(cfg.getMenu());

					} else if (item.equals(cfg.getConfig(GroupConfig.REMOVE_NEGATION))) {

						cfg.setMenu(gh.getNegatedPermissions(cfg.getGroup()), GroupConfig.REMOVE_NEGATION);
						p.openInventory(cfg.getMenu());

					} else if (item.equals(cfg.getConfig(GroupConfig.RANK))) {

						LinkedList<String> list = new LinkedList<>();
						for (int i = 0; i < 11; i++) {
							list.add("" + i);
						}
						cfg.setMenu(list, GroupConfig.RANK);
						p.openInventory(cfg.getMenu());
					} else if (item.equals(cfg.getConfig(GroupConfig.OP))) {
						if (gh.isOP(cfg.getGroup())) {
							gh.setOp(cfg.getGroup(), false);
							item.setType(Material.RED_WOOL);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName("Toggle OP");
							meta.setLore(Arrays.asList("OP - False"));
							item.setItemMeta(meta);
							cfg.setOP(item);
						} else {
							gh.setOp(cfg.getGroup(), true);
							item.setType(Material.GREEN_WOOL);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName("Toggle OP");
							meta.setLore(Arrays.asList("OP - True"));
							item.setItemMeta(meta);
							cfg.setOP(item);
						}
					}
				}
				e.setCancelled(true);
			} else if (e.getInventory() != null && e.getInventory().equals(cfg.getMenu())) {
				if(item != null) {
					if (cfg.getMenuType() == GroupConfig.ADD_PLAYER) {
						if (item.equals(cfg.getConfig(GroupConfig.NEXT))) {
							LinkedList<String> players = ph.getPlayers();
							players.removeAll(getPlayers(cfg.getGroup()));
							cfg.nextPage(players);
						} else if (item.equals(cfg.getConfig(GroupConfig.LAST))) {
							LinkedList<String> players = ph.getPlayers();
							players.removeAll(getPlayers(cfg.getGroup()));
							cfg.previousPage(players);
						} else if (ph.addGroup(cfg.getGroup(), e.getCurrentItem().getItemMeta().getDisplayName())) {
							CMD.sendMessage(p, "§aAdded §6" + e.getCurrentItem().getItemMeta().getDisplayName()
									+ "§a to the group §6" + cfg.getTitle() + "§a!");
						}
					} else if (cfg.getMenuType() == GroupConfig.REMOVE_PLAYER) {
						if (item.equals(cfg.getConfig(GroupConfig.NEXT))) {
							cfg.nextPage(getPlayers(cfg.getGroup()));
						} else if (item.equals(cfg.getConfig(GroupConfig.LAST))) {
							cfg.previousPage(getPlayers(cfg.getGroup()));
						} else if (ph.removeGroup(cfg.getGroup(), e.getCurrentItem().getItemMeta().getDisplayName())) {
							CMD.sendMessage(p, "§aRemoved §6" + e.getCurrentItem().getItemMeta().getDisplayName()
									+ "§a from the group §6" + cfg.getTitle() + "§a!");
						}
					} else if (cfg.getMenuType() == GroupConfig.ADD_PERMISSION) {
						if (item.equals(cfg.getConfig(GroupConfig.NEXT))) {
							LinkedList<String> permissions = new LinkedList<>();
							for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
								PluginDescriptionFile file = pl.getDescription();
								for (String command : file.getCommands().keySet()) {
									String perm = (String) file.getCommands().get(command).get("permission");
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
								for (Permission perms : file.getPermissions()) {
									String perm = perms.getName();
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
							}
							cfg.nextPage(permissions);
						} else if (item.equals(cfg.getConfig(GroupConfig.LAST))) {
							LinkedList<String> permissions = new LinkedList<>();
							for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
								PluginDescriptionFile file = pl.getDescription();
								for (String command : file.getCommands().keySet()) {
									String perm = (String) file.getCommands().get(command).get("permission");
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
								for (Permission perms : file.getPermissions()) {
									String perm = perms.getName();
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
							}
							cfg.previousPage(permissions);
						} else if (gh.addPermission(cfg.getGroup(), e.getCurrentItem().getItemMeta().getDisplayName())) {
							CMD.sendMessage(p, "§aAdded the permission §6" + e.getCurrentItem().getItemMeta().getDisplayName()
									+ "§a to the group §6" + cfg.getTitle() + "§a!");
						}
					} else if (cfg.getMenuType() == GroupConfig.REMOVE_PERMISSION) {
						if (item.equals(cfg.getConfig(GroupConfig.NEXT))) {
							cfg.nextPage(gh.getPermissions(cfg.getGroup()));
						} else if (item.equals(cfg.getConfig(GroupConfig.LAST))) {
							cfg.previousPage(gh.getPermissions(cfg.getGroup()));
						} else if (gh.removePermission(cfg.getGroup(), e.getCurrentItem().getItemMeta().getDisplayName())) {
							CMD.sendMessage(p, "§aRemoved the permission §6" + e.getCurrentItem().getItemMeta().getDisplayName()
									+ "§a from the group §6" + cfg.getTitle() + "§a!");
						}
					} else if (cfg.getMenuType() == GroupConfig.NEGATE) {
						if (item.equals(cfg.getConfig(GroupConfig.NEXT))) {
							LinkedList<String> permissions = new LinkedList<>();
							for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
								PluginDescriptionFile file = pl.getDescription();
								for (String command : file.getCommands().keySet()) {
									String perm = (String) file.getCommands().get(command).get("permission");
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getNegatedPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
								for (Permission perms : file.getPermissions()) {
									String perm = perms.getName();
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getNegatedPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
							}
							cfg.nextPage(permissions);
						} else if (item.equals(cfg.getConfig(GroupConfig.LAST))) {
							LinkedList<String> permissions = new LinkedList<>();
							for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
								PluginDescriptionFile file = pl.getDescription();
								for (String command : file.getCommands().keySet()) {
									String perm = (String) file.getCommands().get(command).get("permission");
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getNegatedPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
								for (Permission perms : file.getPermissions()) {
									String perm = perms.getName();
									if (perm != null) {
										if (!permissions.contains(perm)
												&& !gh.getNegatedPermissions(cfg.getGroup()).contains(perm)) {
											permissions.add(perm);
										}
									}
								}
							}
							cfg.previousPage(permissions);
						} else if (gh.negatePermission(cfg.getGroup(), e.getCurrentItem().getItemMeta().getDisplayName())) {
							CMD.sendMessage(p, "§aNegated the permission §6" + e.getCurrentItem().getItemMeta().getDisplayName()
									+ "§a for the group §6" + cfg.getTitle() + "§a!");
						}
					} else if (cfg.getMenuType() == GroupConfig.REMOVE_NEGATION) {
						if (item.equals(cfg.getConfig(GroupConfig.NEXT))) {
							cfg.nextPage(gh.getNegatedPermissions(cfg.getGroup()));
						} else if (item.equals(cfg.getConfig(GroupConfig.LAST))) {
							cfg.previousPage(gh.getNegatedPermissions(cfg.getGroup()));
						} else if (gh.removeNegatedPermission(cfg.getGroup(), e.getCurrentItem().getItemMeta().getDisplayName())) {
							CMD.sendMessage(p, "§aRemoved the negated permission §6"
									+ e.getCurrentItem().getItemMeta().getDisplayName() + "§a from the group §6"
									+ cfg.getTitle() + "§a!");
						}
					} else if (cfg.getMenuType() == GroupConfig.RANK) {
						if (gh.setRank(cfg.getGroup(),
								Integer.parseInt(e.getCurrentItem().getItemMeta().getDisplayName()))) {
							CMD.sendMessage(p, "§aSet the rank of the group §6" + cfg.getTitle() + " §ato §6"
									+ e.getCurrentItem().getItemMeta().getDisplayName() + "§a!");
						}
					}
				}

				e.getInventory().remove(e.getCurrentItem());

				e.setCancelled(true);
			}
		}
		main.reloadPlayers();
	}

	@EventHandler
	public void signChange(SignChangeEvent e) {
		if (e.getBlock().getLocation().equals(loc)) {
			String s = e.getLine(0) + e.getLine(1) + e.getLine(2) + e.getLine(3);
			if (type == GroupConfig.PREFIX) {
				gh.setPrefix(g, ChatColor.translateAlternateColorCodes('&', s));
				CMD.sendMessage(e.getPlayer(), "§aThe prefix of the group §6" + g.getName() + "§a was set to " + ChatColor.translateAlternateColorCodes('&', s));
			} else if (type == GroupConfig.SUFFIX) {
				gh.setSuffix(g, ChatColor.translateAlternateColorCodes('&', s));
				CMD.sendMessage(e.getPlayer(), "§aThe suffix of the group §6" + g.getName() + "§a was set to " + ChatColor.translateAlternateColorCodes('&', s));
			}
			e.getBlock().setType(Material.AIR);
			loc = null;
			g = null;
			main.reloadPlayers();
		}
	}

	private LinkedList<String> getPlayers(Group g) {
		LinkedList<String> players = new LinkedList<String>();
		for (String s : ph.getPlayers()) {
			if (ph.isInGroup(s, g)) {
				players.add(s);
			}
		}
		return players;
	}

	public void updateGroups() {
		configs = new LinkedList<>();
		for (Group g : main.getGroupHandler().getGroups()) {
			if (g != null)
				configs.add(new GroupConfig(g));
		}
	}

	public Inventory getGroupConfig(String group) {
		for (GroupConfig g : configs) {
			if (group.equals(g.getTitle())) {
				return g.getInv();
			}
		}
		return null;
	}

}
