package de.manator.mypermissions.groups;

/**
 * A class used to represent a group
 * @author ManatorDE
 */
public class Group {
	/**
	 * The rank of the group
	 */
	private int rank;
	
	/**
	 * The prefix of the group
	 */
	private String prefix;
	
	/**
	 * The suffix of the group
	 */
	private String suffix;
	
	/**
	 * The name of the group
	 */
	private String name;
	
	/**
	 * The op state of the group
	 */
	private boolean op;
	
	/**
	 * The constructor of Group
	 * @param name The name of the group
	 * @param rank The rank of the group
	 */
	public Group(String name, int rank) {
		this.setName(name);
		this.setRank(rank);
		this.prefix = null;
		this.suffix = null;
		this.op = false;
	}
	
	/**
	 * Gets the rank of the group
	 * @return The rank of the group
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Sets the rank of the group
	 * @param rank The new rank of the group
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Gets the prefix of the group
	 * @return The prefix of the group
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets the prefix of the group
	 * @param prefix The new prefix of the group
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Gets the suffix of the group
	 * @return The suffix of the group
	 */
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Sets the suffix of the group
	 * @param suffix The new suffix of the group
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	/**
	 * Gets the name of the group
	 * @return The name of the group
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the group
	 * @param name The new name of the group
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the op state of the group
	 * @return True if the group is opped, false if not
	 */
	public boolean isOp() {
		return op;
	}
	
	/**
	 * Sets the op state of the group
	 * @param op The new op state of the group
	 */
	public void setOp(boolean op) {
		this.op = op;
	}
}
