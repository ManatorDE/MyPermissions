package de.manator.mypermissions.players;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.manator.mypermissions.groups.Group;

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
			if(!nperms.exists()) {
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
			if(!cfg.exists()) {
				try {
					cfg.createNewFile();
					BufferedWriter bw = new BufferedWriter(new FileWriter(cfg));
					bw.write("ExcludeFromDefault: false");
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
	}

	public boolean addGroup(Group g, String player) {
		if (playerExists(player)) {
			File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(groups));
				bw.newLine();
				bw.append(g.getName());
				bw.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean removeGroup(Group g, String player) {
		if (playerExists(player)) {
			File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");
			try {
				BufferedReader br = new BufferedReader(new FileReader(groups));
				ArrayList<String> lines = new ArrayList<String>();
				while (br.ready()) {
					String l = br.readLine();
					if (!l.equalsIgnoreCase(g.getName())) {
						lines.add(l);
					}
				}
				br.close();
				BufferedWriter bw = new BufferedWriter(new FileWriter(groups));
				for (String l : lines) {
					bw.write(l);
					bw.newLine();
				}
				bw.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public ArrayList<String> getGroups(String player) {
		ArrayList<String> groupsList = new ArrayList<String>();
		File groups = new File(playersFolder.getAbsolutePath() + "/" + player + "/groups.yml");

		try {
			BufferedReader br = new BufferedReader(new FileReader(groups));
			while (br.ready()) {
				groupsList.add(br.readLine().toUpperCase());
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return groupsList;
	}

	private boolean playerExists(String name) {
		for (File f : playersFolder.listFiles()) {
			if (f.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<String> getPlayers() {
		ArrayList<String> players = new ArrayList<String>();
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
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(perms));
			bw.append("\n" + perm);
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public ArrayList<String> getPermissions(String player) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(perms));
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

	public boolean removePermission(String player, String perm) {
		File perms = new File(playersFolder.getAbsolutePath() + "/" + player + "/permissions.yml");
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(perms));
			while (br.ready()) {
				String s = br.readLine();
				if (!s.equalsIgnoreCase(perm)) {
					list.add(s);
				}
			}
			br.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean negatePermission(String player, String nperm) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(nperms));
			bw.append("\n" + nperm);
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeNegatedPermission(String player, String nperm) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(nperms));
			while (br.ready()) {
				String s = br.readLine();
				if (!s.equalsIgnoreCase(nperm)) {
					list.add(s);
				}
			}
			br.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> getNegatedPermissions(String player) {
		File nperms = new File(playersFolder.getAbsolutePath() + "/" + player + "/negated_permissions.yml");
		ArrayList<String> list = new ArrayList<String>();
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
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(cfg));
			if(excluded) {
				bw.write("ExcludeFromDefault: true");
			} else {
				bw.write("ExcludeFromDefault: false");
			}
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isExcluded(String player) {
		File cfg = new File(playersFolder.getAbsolutePath() + "/" + player + "/config.yml");
		try {
			BufferedReader br = new BufferedReader(new FileReader(cfg));
			String line = br.readLine();
			br.close();
			if(line.endsWith("true")) {
				return true;
			} else if(line.endsWith("false")) {
				return false;
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
