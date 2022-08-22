package de.manator.mypermissions.groups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import de.manator.mypermissions.io.FileHandler;

/**
 * A class used to handle all groups
 * @author ManatorDE
 */
public class GroupHandler {

	/**
	 * The folder to store all group data
	 */
	private File groupFolder;
	
	/**
	 * A list of all groups 
	 */
	private LinkedList<Group> groups;

	/**
	 * The constructor of GroupHandler
	 * @param dataFolder The dataFolder of the plugin
	 */
	public GroupHandler(File dataFolder) {
		this.groupFolder = new File(dataFolder.getAbsolutePath() + "/groups");
		if (!this.groupFolder.exists()) {
			this.groupFolder.mkdirs();
		}
		this.groups = new LinkedList<Group>();
	}

	/**
	 * Adds the group files of a group
	 * @param name The name of the group
	 * @return True if it worked, false if not
	 */
	public boolean addGroup(String name) {
		Group g = new Group(name, 0);
		if (groupExists(g)) {
			return false;
		} else {
			groups.add(g);
			return createGroupFile(g);
		}
	}

	/**
	 * Adds the group files of a group based of a super group
	 * @param name The name of the group
	 * @param sg The super group
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Deletes the group files of a group
	 * @param g The group
	 * @return True if it worked, false if not
	 */
	public boolean deleteGroup(Group g) {
		if (groupExists(g)) {
			File folder = new File(groupFolder.getAbsolutePath() + "/" + g.getName());
			folder.delete();
			groups.remove(getGroup(g.getName()));
			return true;
		}
		return false;
	}

	/**
	 * Loads all group files
	 * @return True if it worked, false if not
	 */
	public boolean loadGroups() {
		File[] groupFiles = groupFolder.listFiles();
		for (File f : groupFiles) {
			if (!f.getName().equalsIgnoreCase("default.yml")) {
				String[] data = new String[5];
				try {
					BufferedReader br = new BufferedReader(new FileReader(new File(f.getAbsolutePath() + "/data.yml")));
					for (int i = 0; i < 5; i++) {
						data[i] = br.readLine();
						if (data[i] != null && data[i].split("\\s+").length > 1) {
							data[i] = data[i].split("\\s+")[1];
						} else {
							data[i] = null;
						}
					}
					br.close();
					if(data[0] != null && data[1] != null && !data[0].equals("") && !data[1].equals("")) {
						Group g = new Group(data[0], Integer.parseInt(data[1]));
						if (data[2] != null && !data[2].equalsIgnoreCase("")) {
							g.setPrefix(data[2]);
						}
						if (data[3] != null && !data[3].equalsIgnoreCase("")) {
							g.setSuffix(data[3]);
						}
						g.setOp(Boolean.parseBoolean(data[4]));
						groups.add(g);
					} else {
						return false;
					}
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

	/**
	 * Sets the prefix of a group
	 * @param g The group
	 * @param prefix The new prefix
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets the prefix of a group
	 * @param g The group
	 * @return The prefix as String
	 */
	public String getPrefix(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).getPrefix();
		} else {
			return null;
		}
	}

	/**
	 * Deletes the prefix of a group
	 * @param g The group
	 * @return True if it worked, false if not
	 */
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
	
	/**
	 * Sets the suffix of a group
	 * @param g The group
	 * @param suffix The new prefix
	 * @return True if it worked, false if not
	 */
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
	
	/**
	 * Gets the suffix of a group
	 * @param g The group
	 * @return The suffix as String
	 */
	public String getSuffix(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).getSuffix();
		} else {
			return null;
		}
	}
	
	/**
	 * Deletes the suffix of a group
	 * @param g The group
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Sets the rank of a group
	 * @param g The group
	 * @param rank The new rank of the group
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets the rank of a group
	 * @param g The group
	 * @return The rank of a group
	 */
	public int getRank(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).getRank();
		} else {
			return -1;
		}
	}

	/**
	 * Adds a permission to a group
	 * @param g The group
	 * @param perm The permission
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Removes a permission from a group
	 * @param g The group
	 * @param perm The permission
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Negates a permission for a group
	 * @param g The group
	 * @param nperm The permission
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Removes a negation for a group
	 * @param g The group
	 * @param nperm The permission
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets the permissions of a group
	 * @param g The group
	 * @return A LinkedList of all permissions of the group as Strings
	 */
	public LinkedList<String> getPermissions(Group g) {
		if (groupExists(g)) {
			File groupPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/permissions.yml");
			return FileHandler.getLines(groupPermissionsFile);
		}
		return new LinkedList<>();
	}
	
	/**
	 * Gets the negated permissions of a group
	 * @param g The group
	 * @return A LinkedList of all negated permissions of the group as Strings
	 */
	public LinkedList<String> getNegatedPermissions(Group g) {
		if (groupExists(g)) {
			File groupNegatedPermissionsFile = new File(
					groupFolder.getAbsolutePath() + "/" + g.getName() + "/negated_permissions.yml");
			return FileHandler.getLines(groupNegatedPermissionsFile);
		}
		return new LinkedList<>();
	}
	
	/**
	 * Sets the op state of the group
	 * @param g The group
	 * @param op The new op state of the group
	 * @return True if it worked, false if not
	 */
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

	/**
	 * gets the op state of a group
	 * @param g The group
	 * @return The op state of a group
	 */
	public boolean isOP(Group g) {
		if (groupExists(g)) {
			return getGroup(g.getName()).isOp();
		} else {
			return false;
		}
	}

	/**
	 * Checks if a group exists
	 * @param g The group
	 * @return True if the group exists, false if not
	 */
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

	/**
	 * A method used to create a groups files
	 * @param g The group
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets a group object by its name
	 * @param name The name of the group
	 * @return The group object
	 */
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

	/**
	 * Setting a group as default group
	 * @param g The group
	 * @return True if it worked, false if not
	 */
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

	/**
	 * Gets the default group
	 * @return The default groups object
	 */
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

	/**
	 * Gets a list of all groups
	 * @return A LinkedList of all group objects
	 */
	public LinkedList<Group> getGroups() {
		LinkedList<Group> gr = new LinkedList<>();
		for (String s : groupFolder.list()) {
			gr.add(getGroup(s));
		}
		return gr;
	}
}
