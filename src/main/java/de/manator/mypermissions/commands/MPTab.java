package de.manator.mypermissions.commands;

import java.util.LinkedList;
import java.util.List;

import de.manator.mypermissions.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

/**
 * The TabCompleter of the mp command
 * @author ManatorDE
 */
public class MPTab implements TabCompleter {

    private final Main main;

    public MPTab(Main main) {
        this.main = main;
    }

	/**
	 * A method used to get a list of possible tab completions for the MP
	 */
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
		LinkedList<String> list = new LinkedList<String>();
		if (command.getName().equalsIgnoreCase("mp")) {
			list = new LinkedList<String>();
			if (args.length == 1) {
                list.add("rl");
                list.add("help");
                list.add("enableEssentialsFix");
                list.add("disableEssentialsFix");
                list.add("setprefixspace");
                list.add("setsuffixspace");
                list.add("setnamecolor");
                list.removeIf(item -> !item.startsWith(args[0]));
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("help")) {
					list.add("1");
					list.add("2");
					list.add("3");
					list.add("4");
                    list.removeIf(item -> !item.startsWith(args[1]));
				} else if(args[0].equalsIgnoreCase("setprefixspace")) {
				    list.add("true");
				    list.add("false");
                    list.removeIf(item -> !item.startsWith(args[1]));
				} else if(args[0].equalsIgnoreCase("setsuffixspace")) {
                    list.add("true");
                    list.add("false");
                    list.removeIf(item -> !item.startsWith(args[1]));
                } else if(args[0].equalsIgnoreCase("setnamecolor")) {
                    list.addAll(main.getPlayerHandler().getPlayers());
                    list.removeIf(item -> !item.startsWith(args[1]));
                }
			} else if (args.length == 3) {
                if(args[0].equalsIgnoreCase("setnamecolor")) {
                    list.add("&r");
                    list.add("&0");
                    list.add("&1");
                    list.add("&2");
                    list.add("&3");
                    list.add("&4");
                    list.add("&5");
                    list.add("&6");
                    list.add("&7");
                    list.add("&8");
                    list.add("&9");
                    list.add("&a");
                    list.add("&b");
                    list.add("&c");
                    list.add("&d");
                    list.add("&e");
                    list.add("&f");
                    list.removeIf(item -> !item.startsWith(args[2]));
                }
            }
		}
		return list;
	}

}
