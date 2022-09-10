package de.manator.mypermissions.config;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import de.manator.mypermissions.io.FileHandler;

/**
 * A class used to controll the config file(s) of MyPermissions
 * @author ManatorDE
 *
 */
public class ConfigFile {
	
	/**
	 * The config file
	 */
	private File config;
	
	/**
	 * The constructor of ConfigFile
	 * @param dataFolder The data folder of MyPermissions
	 * @param name The name of the config file
	 */
	public ConfigFile(File dataFolder, String name) {
		this.config = new File(dataFolder.getAbsolutePath() + "/" + name);
		if(!config.exists()) {
			try {
				config.createNewFile();
				setEssentialsDisplayNameDisabled(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * A method used to set the boolean field "essentails-displayname-disabled" in the config file
	 * @param disabled The boolean value the field will be set to
	 */
	public void setEssentialsDisplayNameDisabled(boolean disabled) {
		setField("essentials-displayname-disabled", disabled);
	}
	
	/**
	 * Gets the value of the field "essentails-displayname-disabled"
	 * @return The boolean value of this field
	 */
	public boolean getEssentialsDisplayNameDisabled() {
		return getField("essentials-displayname-disabled");
	}
	
	/**
	 * A method used to set a boolean field
	 * @param field The fields name
	 * @param state The boolean value
	 */
	private void setField(String field, boolean state) {
		LinkedList<String> lines = getLines();
		boolean lineChanged = false;
		for(int i = 0; i < lines.size(); i++) {
			if(lines.get(i).startsWith(field)) {
				lines.set(i, field + ": " + Boolean.toString(state));
				lineChanged = true;
				break;
			}
		}
		if(!lineChanged) {
			lines.add(field + ": " + Boolean.toString(state));
		}
		writeLines(lines);
	}
	
	/**
	 * Gets the boolean value of a field
	 * @param field The name of the field
	 * @return The boolean value of that field
	 */
	private boolean getField(String field) {
		LinkedList<String> lines = getLines();
		for(int i = 0; i < lines.size(); i++) {
			if(lines.get(i).startsWith(field)) {
				if(lines.get(i).endsWith("true")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the lines of the config file
	 * @return A LinkedList wit all lines of this config file as Strings 
	 */
	private LinkedList<String> getLines() {
		return FileHandler.getLines(config);
	}
	
	/**
	 * Writes the lines from the list into the config file
	 * @param lines The LinkedList containing the lines
	 */
	private void writeLines(LinkedList<String> lines) {
		FileHandler.writeLines(lines, config);
	}
	
}