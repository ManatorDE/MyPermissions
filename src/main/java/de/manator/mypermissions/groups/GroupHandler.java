package de.manator.mypermissions.groups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import de.manator.mypermissions.io.FileHandler;

public class GroupHandler {

	private File groupFolder;
	private LinkedList<Group> groups;

	public GroupHandler(File dataFolder) {
		this.groupFolder = new File(dataFolder.getAbsolutePath() + "/groups");
		if (!this.groupFolder.exists()) {
			this.groupFolder.mkdirs();
		}
		this.groups = new LinkedList<Group>();
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

	public boolean addGroup(String name, Group sg) {
		Group g = new Group(name, 0);
		if (groupExists(g) || !groupExists(sg)) {
			return false;
		} else {
			groups.add(g);
			LinkedList<String> permissions = getPermissions(sg);
			g.setRank(sg.getRank());
			boolean created = createGroupFile(g);
			if (created) {
				for (String s : permissions) {
					addPermission(g, s);
				}
			}
			return created;
		}
	}

	public boolean deleteGroup(Group g) {
		if (groupExists(g)) {
			File folder = new File(groupFolder.getAbsolutePath() + "/" + g.getName());
			folder.delete();
			groups.remove(getGroup(g.getName()));
			return true;
		}
		return false;
	}

	public boolean loadGroups() {
		File[] groupFiles = groupFolder.listFiles();
		for (File f : groupFiles) {
			if (!f.getName().equalsIgnoreCase("default.yml")) {
				String[] data = new String[5];
				try {
					BufferedReader br = new BufferedReader(new FileReader(new File(f.getAbsolutePath() + "/data.yml")));
					for (int i = 0; i < 5; i++) {
						data[i] = br.readLine();
						if (data[i].split("\\s+").length > 1) {
							data[i] = data[i].split("\\s+")[1];
						} else {
							data[i] = null;
						}
					}
					br.close();
					Group g = new Group(data[0], Integer.parseInt(data[1]));
					if (data[2] != null && !data[2].equalsIgnoreCase("")) {
						g.setPrefix(data[2]);
					}
					if (data[3] != null && !data[3].equalsIgnoreCase("")) {
						g.setSuffix(data[3]);
					}
					g.setOp(Boolean.parseBoolean(data[4]));
					groups.add(g);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public boolean setPrefix(Group g, String prefix) {
		if (groupExists(g)) {
			getGroup(g.getName()).setPrefix(prefix);
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			LinkedList<String> lines = FileHandler.getLines(groupDataFile);
			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith("Prefix:")) {
					lines.set(i, "Prefix: " + prefix);
				}
			}
			FileHandler.writeLines(lines, groupDataFile);
			return true;
		} else {
			return false;
		}
	}

	public String getPrefix(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).getPrefix();
		} else {
			return null;
		}
	}

	public boolean deletePrefix(Group g) {
		if (groupExists(g)) {
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			getGroup(g.getName()).setPrefix(null);

			LinkedList<String> lines = FileHandler.getLines(groupDataFile);

			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith("Prefix:")) {
					lines.set(i, "Prefix:");
				}
			}

			FileHandler.writeLines(lines, groupDataFile);
			return true;
		} else {
			return false;
		}
	}

	public boolean setSuffix(Group g, String suffix) {
		if (groupExists(g)) {
			getGroup(g.getName()).setSuffix(suffix);
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			LinkedList<String> lines = FileHandler.getLines(groupDataFile);
			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith("Suffix:")) {
					lines.set(i, "Suffix: " + suffix);
				}
			}
			FileHandler.writeLines(lines, groupDataFile);
			return true;
		} else {
			return false;
		}
	}

	public String getSuffix(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).getSuffix();
		} else {
			return null;
		}
	}

	public boolean deleteSuffix(Group g) {
		if (groupExists(g)) {
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			getGroup(g.getName()).setPrefix(null);

			LinkedList<String> lines = FileHandler.getLines(groupDataFile);

			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith("Suffix:")) {
					lines.set(i, "Suffix:");
				}
			}

			FileHandler.writeLines(lines, groupDataFile);
			return true;
		} else {
			return false;
		}
	}

	public boolean setRank(Group g, int rank) {
		if (groupExists(g)) {
			getGroup(g.getName()).setRank(rank);
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			LinkedList<String> lines = FileHandler.getLines(groupDataFile);

			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith("Rank:")) {
					lines.set(i, "Rank: " + rank);
				}
			}

			FileHandler.writeLines(lines, groupDataFile);

			return true;
		} else {
			return false;
		}
	}

	public int getRank(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).getRank();
		} else {
			return -1;
		}
	}

	public boolean addPermission(Group g, String perm) {
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			LinkedList<String> lines = FileHandler.getLines(groupPermissionsFile);
			if (!lines.contains(perm)) {
				lines.add(perm);
				FileHandler.writeLines(lines, groupPermissionsFile);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean removePermission(Group g, String perm) {
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			LinkedList<String> lines = FileHandler.getLines(groupPermissionsFile);
			if (lines.remove(perm)) {
				FileHandler.writeLines(lines, groupPermissionsFile);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean negatePermission(Group g, String nperm) {
		if (groupExists(g)) {
			File groupNegatedPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/negated_permissions.yml");
			LinkedList<String> lines = FileHandler.getLines(groupNegatedPermissionsFile);
			if (!lines.contains(nperm)) {
				lines.add(nperm);
				FileHandler.writeLines(lines, groupNegatedPermissionsFile);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean removeNegatedPermission(Group g, String nperm) {
		if (groupExists(g)) {
			File groupNegatedPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/negated_permissions.yml");
			LinkedList<String> lines = FileHandler.getLines(groupNegatedPermissionsFile);
			
			if(lines.remove(nperm)) {
				FileHandler.writeLines(lines, groupNegatedPermissionsFile);
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public LinkedList<String> getPermissions(Group g) {
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			return FileHandler.getLines(groupPermissionsFile);
		}
		return new LinkedList<>();
	}

	public LinkedList<String> getNegatedPermissions(Group g) {
		if (groupExists(g)) {
			File groupNegatedPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/negated_permissions.yml");
			return FileHandler.getLines(groupNegatedPermissionsFile);
		}
		return new LinkedList<>();
	}

	public boolean setOp(Group g, boolean op) {
		if (groupExists(g)) {
			File groupDataFile = new File(groupFolder.getAbsolutePath() + "/" + g.getName() + "/data.yml");
			LinkedList<String> lines = FileHandler.getLines(groupDataFile);
			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).startsWith("OP:")) {
					if (op) {
						lines.set(i, "OP: true");
					} else {
						lines.set(i, "OP: false");
					}
				}
			}
			FileHandler.writeLines(lines, groupDataFile);
			getGroup(g.getName()).setOp(op);
			return true;
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
		if (g != null) {
			for (Group gr : groups) {
				if (gr.getName().equalsIgnoreCase(g.getName())) {
					exists = true;
					break;
				}
			}
		}
		return exists;
	}

	private boolean createGroupFile(Group g) {
		File folder = new File(groupFolder.getAbsolutePath() + "/" + g.getName());
		File groupDataFile = new File(folder + "/data.yml");
		File groupPermissionsFile = new File(folder + "/permissions.yml");
		File groupNegatedPermissionsFile = new File(folder.getAbsolutePath() + "/negated_permissions.yml");
		if (folder.exists()) {
			return false;
		} else {
			folder.mkdirs();
			try {
				groupDataFile.createNewFile();
				groupPermissionsFile.createNewFile();
				groupNegatedPermissionsFile.createNewFile();

				if (g.getName() != null) {

					LinkedList<String> lines = new LinkedList<>();

					lines.add("Name: " + g.getName());
					lines.add("Rank: " + g.getRank());
					if (g.getPrefix() != null) {
						lines.add("Prefix: " + g.getPrefix());
					} else {
						lines.add("Prefix: ");
					}
					if (g.getSuffix() != null) {
						lines.add("Suffix: " + g.getSuffix());
					} else {
						lines.add("Suffix: ");
					}
					if (g.isOp()) {
						lines.add("OP: true");
					} else {
						lines.add("OP: false");
					}

					FileHandler.writeLines(lines, groupDataFile);

					return true;
				} else {
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

	public boolean setDefault(Group g) {
		if (groupExists(g)) {
			File defaultGroup = new File(groupFolder.getAbsolutePath() + "/default.yml");
			if (!defaultGroup.exists()) {
				try {
					defaultGroup.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			FileHandler.writeLine(g.getName(), defaultGroup);
			return true;
		} else {
			return false;
		}
	}

	public Group getDefault() {
		File defaultGroup = new File(groupFolder.getAbsolutePath() + "/default.yml");
		if (!defaultGroup.exists()) {
			try {
				defaultGroup.createNewFile();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			LinkedList<String> def = FileHandler.getLines(defaultGroup);
			if(def != null &&  !def.isEmpty()) {
				return getGroup(def.get(0));
			}
			return null;
		}
	}

	public LinkedList<Group> getGroups() {
		LinkedList<Group> gr = new LinkedList<>();
		for (String s : groupFolder.list()) {
			gr.add(getGroup(s));
		}
		return gr;
	}
}
