package de.manator.mypermissions.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.players.PlayerHandler;

public class ExcludeTab implements TabCompleter {
	
	private PlayerHandler ph;
	
	public ExcludeTab(Main main) {
		this.ph = main.getPlayerHandler();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		LinkedList<String> list = new LinkedList<>();
		
		if(cmd.getName().equalsIgnoreCase("excludefromdefault")) {
			if(args.length == 1) {
				list = ph.getPlayers();
				for(int i=0; i < list.size(); i++) {
					if(!list.get(i).startsWith(args[0])) {
						list.remove(i);
					}
				}
			} else if(args.length == 2) {
				list.add("true");
				list.add("false");
			}
		}
		
		return list;
	}

}
