package de.manator.mypermissions.commands;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import de.manator.mypermissions.Main;
import de.manator.mypermissions.config.ConfigFile;



/**
 * The CommandExecutor of the mp command
 * @author ManatorDE
 */
public class MP implements CommandExecutor {

	/**
	 * A reference to the Main object of MyPermissions
	 */
	private Main main;
	
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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("mp")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;

				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						main.getGroupHandler().loadGroups();
						main.reloadPlayers();
						CMD.sendMessage(p, "�aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, "�6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "�6Visit https://manatorde.github.io/mypermissions/ for more information!");
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, "�6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "�6Visit https://dev.manator.de/mypermissions/ for more information!");
						}
					} else if(args[0].equalsIgnoreCase("enableEssentialsFix")) {
						main.getConfigFile().setEssentialsDisplayNameDisabled(true);
						toggleFix(true);
						CMD.sendMessage(p, "�aEssentials fix was enabled!");
					} else if(args[0].equalsIgnoreCase("disableEssentialsFix")) {
						main.getConfigFile().setEssentialsDisplayNameDisabled(false);
						toggleFix(false);
						CMD.sendMessage(p, "�aEssentials fix was disabled!");
					}
				} else if(args.length == 2) {
				    if(args[0].equalsIgnoreCase("setprefixspace")) {
				        ConfigFile cf = main.getConfigFile();
				        if(args[1].equalsIgnoreCase("true")) {
				            cf.setPrefixSpaceEnabled(true);
				            CMD.sendMessage(p, "�aPrefix-Space was enabled!");
				        } else if(args[1].equalsIgnoreCase("false")) {
				            cf.setPrefixSpaceEnabled(false);
				            CMD.sendMessage(p, "�aPrefix-Space was disabled!");
				        }
				    } else if(args[0].equalsIgnoreCase("setsuffixspace")) {
                        ConfigFile cf = main.getConfigFile();
                        if(args[1].equalsIgnoreCase("true")) {
                            cf.setSuffixSpaceEnabled(true);
                            CMD.sendMessage(p, "�aSuffix-Space was enabled!");
                        } else if(args[1].equalsIgnoreCase("false")) {
                            cf.setSuffixSpaceEnabled(false);
                            CMD.sendMessage(p, "�aSuffix-Space was disabled!");
                        }
                    }
				}

			} else if(sender instanceof ConsoleCommandSender) {
				ConsoleCommandSender p = (ConsoleCommandSender) sender;
				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl")) {
						main.getGroupHandler().loadGroups();
						main.reloadPlayers();
						CMD.sendMessage(p, "�aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, "�6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "�6Visit https://manatorde.github.io/mypermissions/ for more information!");
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, "�6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "�6Visit https://manatorde.github.io/mypermissions/ for more information!");
						}
					} else if(args[0].equalsIgnoreCase("enableEssentialsFix")) {
						main.getConfigFile().setEssentialsDisplayNameDisabled(true);
						toggleFix(true);
						CMD.sendMessage(p, "�aEssentials fix was enabled!");
					} else if(args[0].equalsIgnoreCase("disableEssentialsFix")) {
						main.getConfigFile().setEssentialsDisplayNameDisabled(false);
						toggleFix(false);
						CMD.sendMessage(p, "�aEssentials fix was disabled!");
					}
				} else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("setprefixspace")) {
                        ConfigFile cf = main.getConfigFile();
                        if(args[1].equalsIgnoreCase("true")) {
                            cf.setPrefixSpaceEnabled(true);
                            CMD.sendMessage(p, "�aPrefix-Space was enabled!");
                        } else if(args[1].equalsIgnoreCase("false")) {
                            cf.setPrefixSpaceEnabled(false);
                            CMD.sendMessage(p, "�aPrefix-Space was disabled!");
                        }
                    } else if(args[0].equalsIgnoreCase("setsuffixspace")) {
                        ConfigFile cf = main.getConfigFile();
                        if(args[1].equalsIgnoreCase("true")) {
                            cf.setSuffixSpaceEnabled(true);
                            CMD.sendMessage(p, "�aSuffix-Space was enabled!");
                        } else if(args[1].equalsIgnoreCase("false")) {
                            cf.setSuffixSpaceEnabled(false);
                            CMD.sendMessage(p, "�aSuffix-Space was disabled!");
                        }
                    }
                }
			} else if(sender instanceof BlockCommandSender) {
				BlockCommandSender p = (BlockCommandSender) sender;
				if (args.length == 0) {

				} else if (args.length == 1) {
					if (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload")) {
						main.getGroupHandler().loadGroups();
						main.reloadPlayers();
						CMD.sendMessage(p, "�aPlugin was reloaded!");
					} else if (args[0].equalsIgnoreCase("help")) {
						this.commands = main.getCommands();

						if (commands.size() > 5) {
							for(int i = 0; i < 5; i++) {
								String s = commands.get(i);
								CMD.sendMessage(p, "�6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "�6Visit https://manatorde.github.io/mypermissions/ for more information!");
						} else {
							for (String s : commands) {
								CMD.sendMessage(p, "�6/" + s + " - " +  main.getCommand(s).getDescription());
							}
							CMD.sendMessage(p, "�6Visit https://manatorde.github.io/mypermissions/ for more information!");
						}
					} else if(args[0].equalsIgnoreCase("enableEssentialsFix")) {
						main.getConfigFile().setEssentialsDisplayNameDisabled(true);
						toggleFix(true);
						CMD.sendMessage(p, "�aEssentials fix was enabled!");
					} else if(args[0].equalsIgnoreCase("disableEssentialsFix")) {
						main.getConfigFile().setEssentialsDisplayNameDisabled(false);
						toggleFix(false);
						CMD.sendMessage(p, "�aEssentials fix was disabled!");
					}
				} else if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("setprefixspace")) {
                        ConfigFile cf = main.getConfigFile();
                        if(args[1].equalsIgnoreCase("true")) {
                            cf.setPrefixSpaceEnabled(true);
                            CMD.sendMessage(p, "�aPrefix-Space was enabled!");
                        } else if(args[1].equalsIgnoreCase("false")) {
                            cf.setPrefixSpaceEnabled(false);
                            CMD.sendMessage(p, "�aPrefix-Space was disabled!");
                        }
                    } else if(args[0].equalsIgnoreCase("setsuffixspace")) {
                        ConfigFile cf = main.getConfigFile();
                        if(args[1].equalsIgnoreCase("true")) {
                            cf.setSuffixSpaceEnabled(true);
                            CMD.sendMessage(p, "�aSuffix-Space was enabled!");
                        } else if(args[1].equalsIgnoreCase("false")) {
                            cf.setSuffixSpaceEnabled(false);
                            CMD.sendMessage(p, "�aSuffix-Space was disabled!");
                        }
                    }
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
