package de.legoshi.lccore.command;

import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// NEEDED??????
// PARTLY REFACTORED
public class PexCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.pex")) {
            sender.sendMessage(Utils.chat("&cInsufficient Permission!"));
            return true;
        }
        if (args.length <= 3) {
            sender.sendMessage(Utils.chat("&cLC Pex: Invalid syntax (0x01)"));
            return true;
        }
        if (args.length != 4 && args.length > 3 && !args[2].equalsIgnoreCase("group") && args[4].contains("world=")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (args[2].equalsIgnoreCase("prefix")) {
                String[] moreArgs = String.join(" ", args).split(" ", 4);
                Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " meta setprefix " + moreArgs[3]);
                sender.sendMessage(Utils.chat("&aAdded prefix!"));
            } else if (args[2].equalsIgnoreCase("suffix")) {
                String[] moreArgs = String.join(" ", args).split(" ", 4);
                Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " meta setsuffix " + moreArgs[3]);
                sender.sendMessage(Utils.chat("&aAdded suffix!"));
            } else {
                sender.sendMessage(Utils.chat("&cLC Pex: Invalid syntax (0x02)"));
                return true;
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("user")) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (args[2].equalsIgnoreCase("add")) {
                    if (args[3].split("")[0].equalsIgnoreCase("-")) {
                        String perm = args[3].substring(1);
                        Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + perm + " false");
                    } else {
                        Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + args[3] + " true");
                    }
                } else if (args[2].equalsIgnoreCase("remove")) {
                    if (args[3].split("")[0].equalsIgnoreCase("-")) {
                        String perm = args[3].substring(1);
                        Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + perm + " true");
                    } else {
                        Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + args[3] + " false");
                    }
                } else {
                    sender.sendMessage(Utils.chat("&cLC Pex: Invalid syntax (0x03)"));
                    return true;
                }
            }
        } else if (args.length == 5 && args[0].equalsIgnoreCase("user")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (args[2].equalsIgnoreCase("group")) {
                if (args[3].equalsIgnoreCase("set")) {
                    Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " parent set " + args[4]);
                    sender.sendMessage(Utils.chat("&aAdded parent!"));
                    return true;
                }
            } else if (args[2].equalsIgnoreCase("add")) {
                if (args[3].split("")[0].equalsIgnoreCase("-")) {
                    String perm = args[3].substring(1);
                    Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + perm + " false world=" + args[4]);
                } else {
                    Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + args[3] + " true world=" + args[4]);
                }
            } else if (args[2].equalsIgnoreCase("remove")) {
                if (args[3].split("")[0].equalsIgnoreCase("-")) {
                    String perm = args[3].substring(1);
                    Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + perm + " true world=" + args[4]);
                } else {
                    Bukkit.dispatchCommand(sender, "lp user " + player.getName() + " permission set " + args[3] + " false world=" + args[4]);
                }
                sender.sendMessage(Utils.chat("&cLC Pex: Invalid syntax (0x04)"));
            }
        }
        return true;
    }
}