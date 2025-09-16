package de.manator.mypermissions.commands;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

import de.manator.mypermissions.players.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.config.ConfigFile;
import org.jetbrains.annotations.NotNull;


/**
 * The CommandExecutor of the mp command
 * @author ManatorDE
 */
public class MP implements CommandExecutor {

	/**
	 * A reference to the Main object of MyPermissions
	 */
	private final Main main;
	
	/**
	 * A list of all MyPermissions commands
	 */
	private LinkedList<String> commands;

	/**
	 * The constructor of MP
	 * @param main A reference to the Main object of MyPermissions
	 */
	public MP(Main main) {
		this.main = main;
		this.commands = main.getCommands();
	}

	/**
	 * A method that gets called when a command was send
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
		if (command.getName().equalsIgnoreCase("mp")) {
            switch (sender) {
                case Player p -> {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                            main.getGroupHandler().loadGroups();
                            main.reloadPlayers();
                            CMD.sendMessage(p, ChatColor.GREEN + "Plugin was reloaded!");
                        } else if (args[0].equalsIgnoreCase("help")) {
                            this.commands = main.getCommands();

                            if (commands.size() > 5) {
                                for (int i = 0; i < 5; i++) {
                                    String s = commands.get(i);
                                    CMD.sendMessage(p, ChatColor.GOLD + "/" + s + " - " + Objects.requireNonNull(main.getCommand(s)).getDescription());
                                }
                                CMD.sendMessage(p, ChatColor.GOLD + "Visit https://manatorde.github.io/mypermissions/ for more information!");
                            } else {
                                for (String s : commands) {
                                    CMD.sendMessage(p, ChatColor.GOLD + "/" + s + " - " + Objects.requireNonNull(main.getCommand(s)).getDescription());
                                }
                                CMD.sendMessage(p, ChatColor.GOLD + "Visit https://dev.manator.de/mypermissions/ for more information!");
                            }
                        } else if (args[0].equalsIgnoreCase("enableEssentialsFix")) {
                            main.getConfigFile().setEssentialsDisplayNameDisabled(true);
                            toggleFix(true);
                            CMD.sendMessage(p, ChatColor.GREEN + "Essentials fix was enabled!");
                        } else if (args[0].equalsIgnoreCase("disableEssentialsFix")) {
                            main.getConfigFile().setEssentialsDisplayNameDisabled(false);
                            toggleFix(false);
                            CMD.sendMessage(p, ChatColor.GREEN + "Essentials fix was disabled!");
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("setprefixspace")) {
                            ConfigFile cf = main.getConfigFile();
                            if (args[1].equalsIgnoreCase("true")) {
                                cf.setPrefixSpaceEnabled(true);
                                CMD.sendMessage(p, ChatColor.GREEN + "Prefix-Space was enabled!");
                            } else if (args[1].equalsIgnoreCase("false")) {
                                cf.setPrefixSpaceEnabled(false);
                                CMD.sendMessage(p, ChatColor.GREEN + "Prefix-Space was disabled!");
                            }
                        } else if (args[0].equalsIgnoreCase("setsuffixspace")) {
                            ConfigFile cf = main.getConfigFile();
                            if (args[1].equalsIgnoreCase("true")) {
                                cf.setSuffixSpaceEnabled(true);
                                CMD.sendMessage(p, ChatColor.GREEN + "Suffix-Space was enabled!");
                            } else if (args[1].equalsIgnoreCase("false")) {
                                cf.setSuffixSpaceEnabled(false);
                                CMD.sendMessage(p, ChatColor.GREEN + "Suffix-Space was disabled!");
                            }
                        }
                    } else if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("setnamecolor")) {
                            PlayerHandler ph = main.getPlayerHandler();
                            if(args[2].length() == 2 && args[1].startsWith("2")) {
                                char c = args[1].toLowerCase().charAt(1);
                                if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r') {
                                    ChatColor color = ChatColor.getByChar(c);
                                    ph.setPlayerNameColor(args[1], color);
                                    CMD.sendMessage(p, ChatColor.GREEN + "Name color was set to " + color + args[2] + ChatColor.GREEN + "!");
                                } else {
                                    CMD.sendMessage(p, ChatColor.RED + "The color code must be a valid minecraft color code!");
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The color code must be a valid minecraft color code!");
                            }
                        }
                    }
                }
                case ConsoleCommandSender p -> {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("rl")) {
                            main.getGroupHandler().loadGroups();
                            main.reloadPlayers();
                            CMD.sendMessage(p, ChatColor.GREEN + "Plugin was reloaded!");
                        } else if (args[0].equalsIgnoreCase("help")) {
                            this.commands = main.getCommands();

                            if (commands.size() > 5) {
                                for (int i = 0; i < 5; i++) {
                                    String s = commands.get(i);
                                    CMD.sendMessage(p, ChatColor.GOLD + "/" + s + " - " + Objects.requireNonNull(main.getCommand(s)).getDescription());
                                }
                                CMD.sendMessage(p, ChatColor.GOLD + "Visit https://manatorde.github.io/mypermissions/ for more information!");
                            } else {
                                for (String s : commands) {
                                    CMD.sendMessage(p, ChatColor.GOLD + "/" + s + " - " + Objects.requireNonNull(main.getCommand(s)).getDescription());
                                }
                                CMD.sendMessage(p, ChatColor.GOLD + "Visit https://manatorde.github.io/mypermissions/ for more information!");
                            }
                        } else if (args[0].equalsIgnoreCase("enableEssentialsFix")) {
                            main.getConfigFile().setEssentialsDisplayNameDisabled(true);
                            toggleFix(true);
                            CMD.sendMessage(p, ChatColor.GREEN + "Essentials fix was enabled!");
                        } else if (args[0].equalsIgnoreCase("disableEssentialsFix")) {
                            main.getConfigFile().setEssentialsDisplayNameDisabled(false);
                            toggleFix(false);
                            CMD.sendMessage(p, ChatColor.GREEN + "Essentials fix was disabled!");
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("setprefixspace")) {
                            ConfigFile cf = main.getConfigFile();
                            if (args[1].equalsIgnoreCase("true")) {
                                cf.setPrefixSpaceEnabled(true);
                                CMD.sendMessage(p, ChatColor.GREEN + "Prefix-Space was enabled!");
                            } else if (args[1].equalsIgnoreCase("false")) {
                                cf.setPrefixSpaceEnabled(false);
                                CMD.sendMessage(p, ChatColor.GREEN + "Prefix-Space was disabled!");
                            }
                        } else if (args[0].equalsIgnoreCase("setsuffixspace")) {
                            ConfigFile cf = main.getConfigFile();
                            if (args[1].equalsIgnoreCase("true")) {
                                cf.setSuffixSpaceEnabled(true);
                                CMD.sendMessage(p, ChatColor.GREEN + "Suffix-Space was enabled!");
                            } else if (args[1].equalsIgnoreCase("false")) {
                                cf.setSuffixSpaceEnabled(false);
                                CMD.sendMessage(p, ChatColor.GREEN + "Suffix-Space was disabled!");
                            }
                        }
                    } else if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("setnamecolor")) {
                            PlayerHandler ph = main.getPlayerHandler();
                            if(args[2].length() == 2 && args[1].startsWith("2")) {
                                char c = args[1].toLowerCase().charAt(1);
                                if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r') {
                                    ChatColor color = ChatColor.valueOf("§" + c);
                                    ph.setPlayerNameColor(args[1], color);
                                    CMD.sendMessage(p, ChatColor.GREEN + "Name color was set to " + color + args[2] + ChatColor.GREEN + "!");
                                } else {
                                    CMD.sendMessage(p, ChatColor.RED + "The color code must be a valid minecraft color code!");
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The color code must be a valid minecraft color code!");
                            }
                        }
                    }
                }
                case BlockCommandSender p -> {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
                            main.getGroupHandler().loadGroups();
                            main.reloadPlayers();
                            CMD.sendMessage(p, ChatColor.GREEN + "Plugin was reloaded!");
                        } else if (args[0].equalsIgnoreCase("help")) {
                            this.commands = main.getCommands();

                            if (commands.size() > 5) {
                                for (int i = 0; i < 5; i++) {
                                    String s = commands.get(i);
                                    CMD.sendMessage(p, ChatColor.GOLD + "/" + s + " - " + Objects.requireNonNull(main.getCommand(s)).getDescription());
                                }
                                CMD.sendMessage(p, ChatColor.GOLD + "Visit https://manatorde.github.io/mypermissions/ for more information!");
                            } else {
                                for (String s : commands) {
                                    CMD.sendMessage(p, ChatColor.GOLD + "/" + s + " - " + Objects.requireNonNull(main.getCommand(s)).getDescription());
                                }
                                CMD.sendMessage(p, ChatColor.GOLD + "Visit https://manatorde.github.io/mypermissions/ for more information!");
                            }
                        } else if (args[0].equalsIgnoreCase("enableEssentialsFix")) {
                            main.getConfigFile().setEssentialsDisplayNameDisabled(true);
                            toggleFix(true);
                            CMD.sendMessage(p, ChatColor.GREEN + "Essentials fix was enabled!");
                        } else if (args[0].equalsIgnoreCase("disableEssentialsFix")) {
                            main.getConfigFile().setEssentialsDisplayNameDisabled(false);
                            toggleFix(false);
                            CMD.sendMessage(p, ChatColor.GREEN + "Essentials fix was disabled!");
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("setprefixspace")) {
                            ConfigFile cf = main.getConfigFile();
                            if (args[1].equalsIgnoreCase("true")) {
                                cf.setPrefixSpaceEnabled(true);
                                CMD.sendMessage(p, ChatColor.GREEN + "Prefix-Space was enabled!");
                            } else if (args[1].equalsIgnoreCase("false")) {
                                cf.setPrefixSpaceEnabled(false);
                                CMD.sendMessage(p, ChatColor.GREEN + "Prefix-Space was disabled!");
                            }
                        } else if (args[0].equalsIgnoreCase("setsuffixspace")) {
                            ConfigFile cf = main.getConfigFile();
                            if (args[1].equalsIgnoreCase("true")) {
                                cf.setSuffixSpaceEnabled(true);
                                CMD.sendMessage(p, ChatColor.GREEN + "Suffix-Space was enabled!");
                            } else if (args[1].equalsIgnoreCase("false")) {
                                cf.setSuffixSpaceEnabled(false);
                                CMD.sendMessage(p, ChatColor.GREEN + "Suffix-Space was disabled!");
                            }
                        }
                    } else if(args.length == 3) {
                        if(args[0].equalsIgnoreCase("setnamecolor")) {
                            PlayerHandler ph = main.getPlayerHandler();
                            if(args[2].length() == 2 && args[1].startsWith("2")) {
                                char c = args[1].toLowerCase().charAt(1);
                                if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r') {
                                    ChatColor color = ChatColor.valueOf("§" + c);
                                    ph.setPlayerNameColor(args[1], color);
                                    CMD.sendMessage(p, ChatColor.GREEN + "Name color was set to " + color + args[2] + ChatColor.GREEN + "!");
                                } else {
                                    CMD.sendMessage(p, ChatColor.RED + "The color code must be a valid minecraft color code!");
                                }
                            } else {
                                CMD.sendMessage(p, ChatColor.RED + "The color code must be a valid minecraft color code!");
                            }
                        }
                    }
                }
                default -> {
                }
            }
		}
		
		main.reloadPlayers();
		return false;
	}
	
	/**
	 * A method used to toggle the essentials fix
	 * @param toggle boolean that determines if the fix should be activated or not
	 */
	private void toggleFix(boolean toggle) {
		Essentials ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if (ess != null) {
			if(ess.isEnabled()) {
				ess.getConfig().set("change-displayname", !toggle);
				try {
					ess.getConfig().save(new File(ess.getDataFolder() + "/config.yml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				ess.reloadConfig();
				main.reloadPlayers();
			}
		}
	}
}
