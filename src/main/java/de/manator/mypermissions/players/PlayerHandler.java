package de.manator.mypermissions.players;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.io.FileHandler;

public class PlayerHandler {

	private File playersFolder;

	public PlayerHandler(File dataFolder) {
		this.playersFolder = new File(dataFolder.getAbsolutePath() + "/players");
		if (!this.playersFolder.exists()) {
			this.playersFolder.mkdirs();
		}
	}

	public boolean addPlayer(String name) {
		if (playerExists(name)) {
			return false;
		} else {
			File player = new File(playersFolder.getAbsolutePath() + "/" + name);
			File perms = new File(player.getAbsolutePath() + "/permissions.yml");
			File nperms = new File(player.getAbsolutePath() + "/negated_permissions.yml");
			File groups = new File(player.getAbsolutePath() + "/groups.yml");
			File cfg = new File(player.getAbsolutePath() + "/config.yml");
			if (!player.exists()) {
				player.mkdirs();
			} else {
				return false;
			}
			if (!perms.exists()) {
				try {
					perms.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if (!nperms.exists()) {
				try {
					nperms.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if (!groups.exists()) {
				try {
					groups.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if (!cfg.exists()) {
				try {
					cfg.createNewFile();
					FileHandler.writeLine("ExcludeFromDefault: false", cfg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
	}

	public boolean addGroup(Group g, String player) {
		if (playerExists(player) && g != null) {
			File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");
			LinkedList<String> lines = FileHandler.getLines(groups);
			if (!lines.contains(g.getName())) {
				lines.add(g.getName());
				FileHandler.writeLines(lines, groups);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean removeGroup(Group g, String player) {
		if (playerExists(player) && g != null) {
			File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");
			LinkedList<String> lines = FileHandler.getLines(groups);
			if (lines.remove(g.getName())) {
				FileHandler.writeLines(lines, groups);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public LinkedList<String> getGroups(String player) {
		File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");
		LinkedList<String> list = FileHandler.getUpperCaseLines(groups);
		if (list != null) {
			return list;
		}
		return new LinkedList<>();
	}

	private boolean playerExists(String name) {
		for (File f : playersFolder.listFiles()) {
			if (f.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public LinkedList<String> getPlayers() {
		LinkedList<String> players = new LinkedList<String>();
		for (String s : playersFolder.list()) {
			players.add(s);
		}

		return players;
	}

	public boolean isInGroup(String player, Group g) {
		if (g != null && getGroups(player).contains(g.getName().toUpperCase())) {
			return true;
		}
		return false;
	}

	public boolean addPermission(String player, String perm) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		LinkedList<String> lines = FileHandler.getLines(perms);
		if (!lines.contains(perm)) {
			lines.add(perm);
			FileHandler.writeLines(lines, perms);
			return true;
		}
		return false;
	}

	public LinkedList<String> getPermissions(String player) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		LinkedList<String> list = FileHandler.getLines(perms);

		if (list != null) {
			return list;
		}

		return new LinkedList<>();
	}

	public boolean removePermission(String player, String perm) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		LinkedList<String> list = FileHandler.getLines(perms);

		if (list.remove(perm)) {
			FileHandler.writeLines(list, perms);
			return true;
		}
		return false;
	}

	public boolean negatePermission(String player, String nperm) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		LinkedList<String> lines = FileHandler.getLines(nperms);
		if (!lines.contains(nperm)) {
			lines.add(nperm);
			FileHandler.writeLines(lines, nperms);
			return true;
		}
		return false;
	}

	public boolean removeNegatedPermission(String player, String nperm) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		LinkedList<String> list = FileHandler.getLines(nperms);
		if (list.remove(nperm)) {
			FileHandler.writeLines(list, nperms);
			return true;
		}
		return false;
	}

	public LinkedList<String> getNegatedPermissions(String player) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		LinkedList<String> list = new LinkedList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(nperms));
			while (br.ready()) {
				list.add(br.readLine());
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean excludeFromDefault(String player, boolean excluded) {
		File cfg = new File(playersFolder.getAbsolutePath() + "/" + player + "/config.yml");
		if (excluded) {
			FileHandler.writeLine("ExcludeFromDefault: true", cfg);
		} else {
			FileHandler.writeLine("ExcludeFromDefault: false", cfg);
		}
		return true;
	}

	public boolean isExcluded(String player) {
		File cfg = new File(playersFolder.getAbsolutePath() + "/" + player + "/config.yml");
		
		LinkedList<String> lines = FileHandler.getLines(cfg);
		if(lines != null && !lines.isEmpty()) {
			return lines.get(0).endsWith("true");
		}
		return false;
	}
}
