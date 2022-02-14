package de.manator.mypermissions.groups;

public class Group {
	private int rank;
	private String prefix, suffix, name;
	private boolean op;
	
	public Group(String name, int rank) {
		this.setName(name);
		this.setRank(rank);
		this.prefix = null;
		this.suffix = null;
		this.op = false;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOp() {
		return op;
	}
	
	public void setOp(boolean op) {
		this.op = op;
	}
}
