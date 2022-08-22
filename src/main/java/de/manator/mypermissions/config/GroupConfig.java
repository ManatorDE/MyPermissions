package de.manator.mypermissions.config;

import java.util.Arrays;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.manator.mypermissions.groups.Group;

/**
 * A class used to configure a group
 * @author ManatorDE
 */
public class GroupConfig {

	/**
	 * A reference to the config inventory
	 */
	private Inventory inv;
	
	/**
	 * A reference to the menu inventory
	 */
	private Inventory menu;
	
	/**
	 * A reference to the group that gets edited 
	 */
	private Group g;
	
	/**
	 * The title of the config inventory 
	 */
	private String title;

	/**
	 * int used to store the last edited menu type
	 */
	private int menuType;
	
	
	/**
	 * ItemStack used to get to the prefix menu
	 */
	private ItemStack prefix;
	
	/**
	 * ItemStack used to get to the suffix menu
	 */
	private ItemStack suffix;
	
	/**
	 * ItemStack used to get to the add permissions menu
	 */
	private ItemStack addPerm;
	
	/**
	 * ItemStack used to get to the remove permissions menu
	 */
	private ItemStack remPerm;
	
	/**
	 * ItemStack used to get to the negate permissions menu
	 */
	private ItemStack negate;
	
	/**
	 * ItemStack used to get to the remove negation menu
	 */
	private ItemStack remNeg;

	/**
	 * ItemStack used to get to the add player menu
	 */
	private ItemStack addPlayer;
	
	/**
	 * ItemStack used to get to the remove player menu
	 */
	private ItemStack remPlayer;
	
	/**
	 * ItemStack used to get to the rank menu
	 */
	private ItemStack rank;
	
	/**
	 * ItemStack used to get to the op menu
	 */
	private ItemStack op;
	
	/**
	 * ItemStack used to get to the default group menu
	 */
	private ItemStack def;
	
	/**
	 * ItemStack used to get to the next menu page
	 */
	private ItemStack next;
	
	/**
	 * ItemStack used to get to the previous menu page
	 */
	private ItemStack last;
	
	/**
	 * Menu type PREFIX
	 */
	public static final int PREFIX = 0;
	
	/**
	 * Menu type SUFFIX
	 */
	public static final int SUFFIX = 1;
	
	/**
	 * Menu type ADD_PERMISSIONS
	 */
	public static final int ADD_PERMISSION = 2;
	
	/**
	 * Menu type REMOVE_PERMISSIONS
	 */
	public static final int REMOVE_PERMISSION = 3;
	
	/**
	 * Menu type NEGATE
	 */
	public static final int NEGATE = 4;
	
	/**
	 * Menu type REMOVE_NEGATION
	 */
	public static final int REMOVE_NEGATION = 5;
	
	/**
	 * Menu type ADD_PLAYER
	 */
	public static final int ADD_PLAYER = 6;
	
	/**
	 * Menu type REMOVE_PLAYER
	 */
	public static final int REMOVE_PLAYER = 7;
	
	/**
	 * Menu type RANK
	 */
	public static final int RANK = 8;
	
	/**
	 * Menu type OP
	 */
	public static final int OP = 9;
	
	/**
	 * Menu type DEFAULT
	 */
	public static final int DEFAULT = 10;
	
	/**
	 * Menu type NEXT
	 */
	public static final int NEXT = 100;
	
	/**
	 * Menu type LAST
	 */
	public static final int LAST = 101;

	/**
	 * The constructor of GroupConfig
	 * @param group The group to be configured
	 */
	public GroupConfig(Group group) {
		g = group;
		this.title = g.getName();
		inv = Bukkit.createInventory(null, 18, title);
		init();
	}
	
	/**
	 * Gets the group of this config
	 * @return The group of this config
	 */
	public Group getGroup() {
		return g;
	}

	/**
	 * Gets the title of this config
	 * @return The title of this config
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the config inventory
	 * @return The config inventory
	 */
	public Inventory getInv() {
		return inv;
	}

	/**
	 * Gets the current config ItemStack
	 * @param cfg The wanted config
	 * @return The config ItemStack
	 */
	public ItemStack getConfig(int cfg) {
		switch (cfg) {
		case PREFIX:
			return prefix;
		case SUFFIX:
			return suffix;
		case ADD_PERMISSION:
			return addPerm;
		case REMOVE_PERMISSION:
			return remPerm;
		case NEGATE:
			return negate;
		case REMOVE_NEGATION:
			return remNeg;
		case ADD_PLAYER:
			return addPlayer;
		case REMOVE_PLAYER:
			return remPlayer;
		case RANK:
			return rank;
		case OP:
			return op;
		case DEFAULT:
			return def;
		case NEXT:
			return next;
		case LAST:
			return last;
		default:
			return null;
		}
	}

	/**
	 * A method used to initialize this config
	 */
	private void init() {
		prefix = new ItemStack(Material.NAME_TAG);
		ItemMeta pmeta = prefix.getItemMeta();
		pmeta.setDisplayName("Set Prefix");
		pmeta.setLore(Arrays.asList("Set Prefix"));
		prefix.setItemMeta(pmeta);

		inv.setItem(0, prefix);

		suffix = new ItemStack(Material.NAME_TAG);
		ItemMeta smeta = suffix.getItemMeta();
		smeta.setDisplayName("Set Suffix");
		smeta.setLore(Arrays.asList("Set Suffix"));
		suffix.setItemMeta(smeta);

		inv.setItem(1, suffix);

		addPerm = new ItemStack(Material.GREEN_BANNER);
		ItemMeta aMeta = addPerm.getItemMeta();
		aMeta.setDisplayName("Add Permission");
		aMeta.setLore(Arrays.asList("Add Permission"));
		addPerm.setItemMeta(aMeta);

		inv.setItem(2, addPerm);

		remPerm = new ItemStack(Material.RED_BANNER);
		ItemMeta rMeta = remPerm.getItemMeta();
		rMeta.setDisplayName("Remove Permission");
		rMeta.setLore(Arrays.asList("Remove Permission"));
		remPerm.setItemMeta(rMeta);

		inv.setItem(3, remPerm);

		negate = new ItemStack(Material.BARRIER);
		ItemMeta negMeta = negate.getItemMeta();
		negMeta.setDisplayName("Negate Permission");
		negMeta.setLore(Arrays.asList("Negate Permission"));
		negate.setItemMeta(negMeta);

		inv.setItem(4, negate);

		remNeg = new ItemStack(Material.BARRIER);
		ItemMeta rnMeta = remNeg.getItemMeta();
		rnMeta.setDisplayName("Remove Negation");
		rnMeta.setLore(Arrays.asList("Remove Negation"));
		remNeg.setItemMeta(rnMeta);

		inv.setItem(5, remNeg);

		addPlayer = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta apMeta = addPlayer.getItemMeta();
		apMeta.setDisplayName("Add Player");
		apMeta.setLore(Arrays.asList("Add Player"));
		addPlayer.setItemMeta(apMeta);

		inv.setItem(6, addPlayer);

		remPlayer = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta rpMeta = remPlayer.getItemMeta();
		rpMeta.setDisplayName("Remove Player");
		rpMeta.setLore(Arrays.asList("Remove Player"));
		remPlayer.setItemMeta(rpMeta);

		inv.setItem(7, remPlayer);

		rank = new ItemStack(Material.BELL);
		ItemMeta rankMeta = rank.getItemMeta();
		rankMeta.setDisplayName("Set Rank");
		rankMeta.setLore(Arrays.asList("Set Rank"));
		rank.setItemMeta(rankMeta);

		inv.setItem(8, rank);

		if (g.isOp()) {
			op = new ItemStack(Material.GREEN_WOOL);
			ItemMeta opMeta = op.getItemMeta();
			opMeta.setDisplayName("Toggle OP");
			opMeta.setLore(Arrays.asList("OP - TRUE"));
			op.setItemMeta(opMeta);
		} else {
			op = new ItemStack(Material.RED_WOOL);
			ItemMeta opMeta = op.getItemMeta();
			opMeta.setDisplayName("Toggle OP");
			opMeta.setLore(Arrays.asList("OP - FALSE"));
			op.setItemMeta(opMeta);
		}

		inv.setItem(9, op);

		def = new ItemStack(Material.WHITE_WOOL);
		ItemMeta defMeta = def.getItemMeta();
		defMeta.setDisplayName("Set Default Group");
		defMeta.setLore(Arrays.asList("Set Default Group"));
		def.setItemMeta(defMeta);

		inv.setItem(10, def);

		next = new ItemStack(Material.WARPED_STEM);
		ItemMeta nextMeta = next.getItemMeta();
		nextMeta.setDisplayName("Next Page");
		nextMeta.setLore(Arrays.asList("Next Page"));
		next.setItemMeta(nextMeta);

		last = new ItemStack(Material.CRIMSON_STEM);
		ItemMeta lastMeta = last.getItemMeta();
		lastMeta.setDisplayName("Last Page");
		lastMeta.setLore(Arrays.asList("Last Page"));
		last.setItemMeta(lastMeta);
	}

	/**
	 * A method used to set the current menu
	 * @param settings The settings of this menu
	 * @param menuType The type of the menu
	 */
	public void setMenu(LinkedList<String> settings, int menuType) {
		int size = settings.size() / 9;
		size++;
		if (size > 6) {
			size = 6;
		}
		Material mat = null;
		if (menuType == ADD_PLAYER) {
			menu = Bukkit.createInventory(null, size * 9, "Add Player");
			mat = Material.PLAYER_HEAD;
		} else if (menuType == REMOVE_PLAYER) {
			menu = Bukkit.createInventory(null, size * 9, "Remove Player");
			mat = Material.PLAYER_HEAD;
		} else if (menuType == ADD_PERMISSION) {
			menu = Bukkit.createInventory(null, size * 9, "Add Permission");
			mat = Material.GREEN_BANNER;
		} else if (menuType == REMOVE_PERMISSION) {
			menu = Bukkit.createInventory(null, size * 9, "Remove Permission");
			mat = Material.RED_BANNER;
		} else if (menuType == NEGATE) {
			menu = Bukkit.createInventory(null, size * 9, "Negate");
			mat = Material.BARRIER;
		} else if (menuType == REMOVE_NEGATION) {
			menu = Bukkit.createInventory(null, size * 9, "Remove Negation");
			mat = Material.BARRIER;
		} else if (menuType == RANK) {
			menu = Bukkit.createInventory(null, size * 9, "Remove Negation");
			mat = Material.BELL;
		}
		this.menuType = menuType;

		for (int i = 0; i < settings.size(); i++) {
			if (i == 53 && settings.size() > 54) {
				menu.addItem(next);
				break;
			} else {
				if (mat == Material.PLAYER_HEAD) {
					ItemStack item = new ItemStack(mat);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					meta.setDisplayName(settings.get(i));
					meta.setOwnerProfile(Bukkit.getServer().createPlayerProfile(settings.get(i)));
					item.setItemMeta(meta);
					menu.addItem(item);
				} else {
					ItemStack item = new ItemStack(mat);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(settings.get(i));
					item.setItemMeta(meta);
					menu.addItem(item);
				}
			}
		}
	}

	/**
	 * Gets the menu inventory
	 * @return The menu inventory
	 */
	public Inventory getMenu() {
		return menu;
	}

	/**
	 * Gets the menu type
	 * @return The menu type
	 */
	public int getMenuType() {
		return menuType;
	}

	/**
	 * Sets the op ItemStack
	 * @param op The new op ItemStack
	 */
	public void setOP(ItemStack op) {
		this.op = op;
	}
	
	/**
	 * Gets the next page of the current menu
	 * @param linkedList The settings of the current menu
	 */
	public void nextPage(LinkedList<String> linkedList) {
		if (linkedList.size() > 54) {
			Material mat = null;
			ItemStack lastItem = null;
			if (menu.getItem(53).equals(next)) {
				lastItem = menu.getItem(51);
			} else {
				lastItem = menu.getItem(52);
			}
			int it = 0;
			while (it < linkedList.size() && linkedList.get(it) != null
					&& !lastItem.getItemMeta().getDisplayName().equals(linkedList.get(it))) {
				it++;
			}
			if (lastItem.getItemMeta().getDisplayName().equals(linkedList.get(it))) {
				it++;
			}
			if (linkedList.get(it) != null) {
				if (menuType == ADD_PLAYER) {
					mat = Material.PLAYER_HEAD;
				} else if (menuType == REMOVE_PLAYER) {
					mat = Material.PLAYER_HEAD;
				} else if (menuType == ADD_PERMISSION) {
					mat = Material.GREEN_BANNER;
				} else if (menuType == REMOVE_PERMISSION) {
					mat = Material.RED_BANNER;
				} else if (menuType == NEGATE) {
					mat = Material.BARRIER;
				} else if (menuType == REMOVE_NEGATION) {
					mat = Material.BARRIER;
				} else if (menuType == RANK) {
					mat = Material.BELL;
				}
				menu.clear();
				for (int i = it; i < linkedList.size(); i++) {

					if (mat == Material.PLAYER_HEAD) {
						ItemStack item = new ItemStack(mat);
						SkullMeta meta = (SkullMeta) item.getItemMeta();
						meta.setDisplayName(linkedList.get(i));
						meta.setOwnerProfile(Bukkit.getServer().createPlayerProfile(linkedList.get(i)));
						item.setItemMeta(meta);
						menu.addItem(item);
					} else {
						ItemStack item = new ItemStack(mat);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(linkedList.get(i));
						item.setItemMeta(meta);
						menu.addItem(item);
					}
				}
				menu.setItem(53, next);
				menu.setItem(52, last);
			}
		}

	}
	
	/**
	 * Gets the previous page of the current menu
	 * @param linkedList The settings of the current menu
	 */
	public void previousPage(LinkedList<String> linkedList) {
		if (linkedList.size() > 54) {
			Material mat = null;

			ItemStack lastItem = null;
			int it = 0;
			int amount = 0;
			for (int i = 0; i < 54; i++) {
				if (menu.getItem(i) == null) {
					lastItem = menu.getItem(i - 1);
					amount = i - 1;
					break;
				}
			}

			while (it < linkedList.size() && !lastItem.getItemMeta().getDisplayName().equals(linkedList.get(it))) {
				it++;
			}

			if (lastItem.getItemMeta().getDisplayName().equals(linkedList.get(it))) {
				it++;
			}

			if (menuType == ADD_PLAYER) {
				mat = Material.PLAYER_HEAD;
			} else if (menuType == REMOVE_PLAYER) {
				mat = Material.PLAYER_HEAD;
			} else if (menuType == ADD_PERMISSION) {
				mat = Material.GREEN_BANNER;
			} else if (menuType == REMOVE_PERMISSION) {
				mat = Material.RED_BANNER;
			} else if (menuType == NEGATE) {
				mat = Material.BARRIER;
			} else if (menuType == REMOVE_NEGATION) {
				mat = Material.BARRIER;
			} else if (menuType == RANK) {
				mat = Material.BELL;
			}
			menu.clear();
			for (int i = it - 51 - amount; i < linkedList.size(); i++) {
				ItemStack item = new ItemStack(mat);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(linkedList.get(i));
				item.setItemMeta(meta);
				menu.addItem(item);
			}
			menu.setItem(52, last);
			menu.setItem(53, next);
		}

	}
}
