package de.manator.mypermissions.groups;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// ToDo's: Prefixes and suffixes and ranks

public class GroupHandler {

	private File groupFolder;
	private ArrayList<Group> groups;

	public GroupHandler(File dataFolder) {
		this.groupFolder = new File(dataFolder.getAbsolutePath() + "/groups");
		if (!this.groupFolder.exists()) {
			this.groupFolder.mkdirs();
		}
		this.groups = new ArrayList<Group>();
	}

	public boolean addGroup(String name) {
		Group g = new Group(name, 0);
		if (groupExists(g)) {
			return false;
		} else {
			groups.add(g);
			return createGroupFile(g);
		}
	}

	public boolean deleteGroup(Group g) {
		if(groupExists(g)) {
			File folder = new File(groupFolder.getAbsolutePath() + "/" + g.getName());
			folder.delete();
			groups.remove(getGroup(g.getName()));
			return true;
		}
		return false;
	}

	public boolean addPermission(Group g, String perm) {
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(groupPermissionsFile));
				bw.append(perm);
				bw.newLine();
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

	public boolean removePermission(Group g, String perm) {
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			try {
				BufferedReader br = new BufferedReader(new FileReader(groupPermissionsFile));
				ArrayList<String> lines = new ArrayList<String>();
				while (br.ready()) {
					String l = br.readLine();
					if (!l.equalsIgnoreCase(perm)) {
						lines.add(l);
					}
				}
				br.close();
				BufferedWriter bw = new BufferedWriter(new FileWriter(groupPermissionsFile));
				for (String l : lines) {
					bw.write(l);
					bw.newLine();
				}
				bw.close();
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public ArrayList<String> getPermissions(Group g) {
		ArrayList<String> perms = new ArrayList<String>();
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			try {
				BufferedReader br = new BufferedReader(new FileReader(groupPermissionsFile));
				while (br.ready()) {
					perms.add(br.readLine());
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return perms;
	}

	public boolean setOp(Group g, boolean op) {
		if (groupExists(g)) {
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			ArrayList<String> lines = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(groupDataFile));
				while (br.ready()) {
					lines.add(br.readLine());
				}
				br.close();
				BufferedWriter bw = new BufferedWriter(new FileWriter(groupDataFile));
				for (String l : lines) {
					if (l.startsWith("OP:")) {
						if (op) {
							bw.write("OP: true");
						} else {
							bw.write("OP: false");
						}
					} else {
						bw.write(l);
					}
					bw.newLine();
				}
				bw.close();
				getGroup(g.getName()).setOp(op);
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isOP(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).isOp();
		} else {
			return false;
		}
	}

	private boolean groupExists(Group g) {
		boolean exists = false;
		for (Group gr : groups) {
			if (gr.getName().equalsIgnoreCase(g.getName())) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	private boolean createGroupFile(Group g) {
		File folder = new File(groupFolder.getAbsolutePath() + "/" + g.getName());
		File groupDataFile = new File(folder + "data.yml");
		File groupPermissionsFile = new File(folder + "permissions.yml");
		if (folder.exists()) {
			return false;
		} else {
			folder.mkdirs();
			try {
				groupDataFile.createNewFile();
				groupPermissionsFile.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(groupDataFile));
				if (g.getName() != null) {
					bw.write("Name: " + g.getName());
					bw.newLine();
					bw.write("Rank: " + g.getRank());
					bw.newLine();
					if (g.getPrefix() != null) {
						bw.write("Prefix: " + g.getPrefix());
					} else {
						bw.write("Prefix: ");
					}
					bw.newLine();
					if (g.getSuffix() != null) {
						bw.write("Suffix: " + g.getSuffix());
					} else {
						bw.write("Suffix: ");
					}
					bw.newLine();
					if (g.isOp()) {
						bw.write("OP: true");
					} else {
						bw.write("OP: false");
					}
					bw.close();
					return true;
				} else {
					bw.close();
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public Group getGroup(String name) {
		Group g = null;
		for (Group gr : groups) {
			if (gr.getName().equalsIgnoreCase(name)) {
				g = gr;
				break;
			}
		}
		return g;
	}
}
