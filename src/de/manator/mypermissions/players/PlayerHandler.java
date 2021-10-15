package de.manator.mypermissions.players;

import java.io.File;
import java.io.IOException;

// ToDo's: Adding permission and group handling
public class PlayerHandler {
	
	private File playersFolder;
	
	public PlayerHandler(File dataFolder) {
		this.playersFolder = new File(dataFolder.getAbsolutePath()  + "/players");
		if(!this.playersFolder.exists()) {
			this.playersFolder.mkdirs();
		}
	}
	
	public boolean addPlayer(String name) {
		if(playerExists(name)) {
			return false;
		} else {
			File player = new File(playersFolder.getAbsolutePath() + "/" + name);
			File perms = new File(player.getAbsolutePath() + "permissions.yml");
			File groups = new File(player.getAbsolutePath() + "groups.yml");
			if(!player.exists()) {
				player.mkdirs();
			} else {
				return false;
			}
			if(!perms.exists()) {
				try {
					perms.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if(!groups.exists()) {
				try {
					groups.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
	}

	private boolean playerExists(String name) {
		for(File f : playersFolder.listFiles()) {
			if(f.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}
