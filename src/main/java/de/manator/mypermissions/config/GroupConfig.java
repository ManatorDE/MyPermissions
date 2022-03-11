package de.manator.mypermissions.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.manator.mypermissions.groups.Group;

public class GroupConfig {

	private Inventory inv;
	private Inventory menu;
	private Group g;
	private String title;

	private int menuType;

	private ItemStack prefix;
	private ItemStack suffix;
	private ItemStack addPerm;
	private ItemStack remPerm;
	private ItemStack negate;
	private ItemStack remNeg;
	private ItemStack addPlayer;
	private ItemStack remPlayer;
	private ItemStack rank;
	private ItemStack op;
	private ItemStack def;
	private ItemStack next;
	private ItemStack last;

	public static final int PREFIX = 0;
	public static final int SUFFIX = 1;
	public static final int ADD_PERMISSION = 2;
	public static final int REMOVE_PERMISSION = 3;
	public static final int NEGATE = 4;
	public static final int REMOVE_NEGATION = 5;
	public static final int ADD_PLAYER = 6;
	public static final int REMOVE_PLAYER = 7;
	public static final int RANK = 8;
	public static final int OP = 9;
	public static final int DEFAULT = 10;
	public static final int NEXT = 100;
	public static final int LAST = 101;

	public GroupConfig(Group group) {
		g = group;
		this.title = g.getName();
		inv = Bukkit.createInventory(null, 18, title);
		init();
	}

	public Group getGroup() {
		return g;
	}

	public String getTitle() {
		return title;
	}

	public Inventory getInv() {
		return inv;
	}

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

	public void setMenu(ArrayList<String> settings, int menuType) {
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
			if (i == 53) {
				if (settings.size() > 54) {
					menu.addItem(next);
					break;
				}
			} else {
				ItemStack item = new ItemStack(mat);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(settings.get(i));
				item.setItemMeta(meta);
				menu.addItem(item);
			}
		}
	}

	public Inventory getMenu() {
		return menu;
	}

	public int getMenuType() {
		return menuType;
	}

	public void setOP(ItemStack op) {
		this.op = op;
	}

	public void nextPage(ArrayList<String> settings) {
		if (settings.size() > 54) {
			Material mat = null;
			ItemStack lastItem = null;
			if (menu.getItem(53).equals(next)) {
				lastItem = menu.getItem(51);
			} else {
				lastItem = menu.getItem(52);
			}
			int it = 0;
			while (settings.get(it) != null && !lastItem.getItemMeta().getDisplayName().equals(settings.get(it))) {
				it++;
			}
			if (settings.get(it) != null) {
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
				for (int i = it; i < settings.size(); i++) {
					ItemStack item = new ItemStack(mat);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(settings.get(i));
					item.setItemMeta(meta);
					menu.addItem(item);
				}
				menu.setItem(53, next);
				menu.setItem(52, last);
			}
		}

	}

	public void previousPage(ArrayList<String> settings) {
		if (settings.size() > 54) {
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

			while (!lastItem.getItemMeta().getDisplayName().equals(settings.get(it))) {
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
			for (int i = it - 51 - amount; i < settings.size(); i++) {
				ItemStack item = new ItemStack(mat);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(settings.get(i));
				item.setItemMeta(meta);
				menu.addItem(item);
			}
			menu.setItem(52, last);
			menu.setItem(53, next);
		}

	}
}
