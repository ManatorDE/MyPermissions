package de.manator.mypermissions.players;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import de.manator.mypermissions.groups.Group;
import de.manator.mypermissions.io.FileHandler;

/**
 * A class used to handle all player files
 * @author ManatorDE
 */
public class PlayerHandler {

	/**
	 * A reference to the folder to store all player data
	 */
	private File playersFolder;

	/**
	 * The constructor of the PlayerHandler
	 * @param dataFolder The plugins data folder
	 */
	public PlayerHandler(File dataFolder) {
		this.playersFolder = new File(dataFolder.getAbsolutePath() + "/players");
		if (!this.playersFolder.exists()) {
			this.playersFolder.mkdirs();
		}
	}

	/**
	 * Adds the player files of a player for MyPermissions 
	 * @param name The name of the player
	 * @return True if files were added, false if not
	 */
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

	/**
	 * Adds a group to a players group file
	 * @param g The name of the group
	 * @param player The name of the player
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Removes a group from a players group file
	 * @param g The name of the group
	 * @param player The name of the player
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets a list of all groups from a players group file
	 * @param player The name of the player
	 * @return LinkedList with all group names as Strings
	 */
	public LinkedList<String> getGroups(String player) {
		File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");
		LinkedList<String> list = FileHandler.getUpperCaseLines(groups);
		if (list != null) {
			return list;
		}
		return new LinkedList<>();
	}

	/**
	 * Checks if the files for that player already exist
	 * @param name The name of the player
	 * @return True if the player exists, false if not
	 */
	private boolean playerExists(String name) {
		File player = new File(playersFolder.getAbsolutePath() + "/" + name);
		return player.exists();
	}
	
	
	/**
	 * Gets a list of all players known to MyPermissions
	 * @return LinkedList with player names as Strings
	 */
	public LinkedList<String> getPlayers() {
		LinkedList<String> players = new LinkedList<String>();
		for (String s : playersFolder.list()) {
			players.add(s);
		}

		return players;
	}

	/**
	 * Checks if a  player is in a specific group
	 * @param player The name of the player
	 * @param g The name of the group
	 * @return True if the player is in the group, false if not
	 */
	public boolean isInGroup(String player, Group g) {
		if (g != null && getGroups(player).contains(g.getName().toUpperCase())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a permission to the permissions file of a player
	 * @param player The name of the player
	 * @param perm The permission
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets a list of all permissions from the permissions file of a player
	 * @param player The name of the player
	 * @return LinkedList with the permissions of the player as Strings
	 */
	public LinkedList<String> getPermissions(String player) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		LinkedList<String> list = FileHandler.getLines(perms);

		if (list != null) {
			return list;
		}

		return new LinkedList<>();
	}

	/**
	 * Removes a permission from the permissions file of a player
	 * @param player The name of the player
	 * @param perm The permission
	 * @return True if it worked, false if not
	 */
	public boolean removePermission(String player, String perm) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		LinkedList<String> list = FileHandler.getLines(perms);

		if (list.remove(perm)) {
			FileHandler.writeLines(list, perms);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a permission to the negated permissions file of a player
	 * @param player The name of the player
	 * @param nperm The permission
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Removes a permission from the negated permissions file of a player
	 * @param player The name of the player
	 * @param nperm The permission
	 * @return True if it worked, false if not
	 */
	public boolean removeNegatedPermission(String player, String nperm) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		LinkedList<String> list = FileHandler.getLines(nperms);
		if (list.remove(nperm)) {
			FileHandler.writeLines(list, nperms);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets a list of all permissions from the negated permissions file of a player
	 * @param player The name of the player
	 * @return LinkedList with the permissions of the player as Strings
	 */
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
	
	/**
	 * Excludes a player from being added to the default group
	 * @param player The name of the player
	 * @param excluded True if the player should be excluded, false if not
	 * @return True if it worked
	 */
	public boolean excludeFromDefault(String player, boolean excluded) {
		File cfg = new File(playersFolder.getAbsolutePath() + "/" + player + "/config.yml");
		if (excluded) {
			FileHandler.writeLine("ExcludeFromDefault: true", cfg);
		} else {
			FileHandler.writeLine("ExcludeFromDefault: false", cfg);
		}
		return true;
	}

	/**
	 * Checks if a player is excluded from being added to the default group
	 * @param player The name of the player
	 * @return True if the player is excluded, false if not
	 */
	public boolean isExcluded(String player) {
		File cfg = new File(playersFolder.getAbsolutePath() + "/" + player + "/config.yml");
		
		LinkedList<String> lines = FileHandler.getLines(cfg);
		if(lines != null && !lines.isEmpty()) {
			return lines.get(0).endsWith("true");
		}
		return false;
	}

	/**
	 * Writes all permissions from the list into the player permission file
	 * @param perms A list of permissions
	 * @param name The name of the player
	 */
	public boolean setPermissions(LinkedList<String> perms, String name) {
		if(perms != null) {
			File pperms = new File(playersFolder.getAbsolutePath() + "/" + name + "/permissions.yml");
			if(playerExists(name)) {
				FileHandler.writeLines(perms, pperms);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Writes all permissions from the list into the player negated permission file
	 * @param perms A list of permissions
	 * @param name The name of the player
	 */
	public boolean setNegatedPermissions(LinkedList<String> perms, String name) {
		if(perms != null) {
			File pperms = new File(playersFolder.getAbsolutePath() + "/" + name + "/negated_permissions.yml");
			if(playerExists(name)) {
				FileHandler.writeLines(perms, pperms);
				return true;
			}
		}
		return false;
	}
}
