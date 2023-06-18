package de.legoshi.lccore.command;

import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// BRO....
// PARTLY REFACTORED
// APPREARS TO BE NOT REGISTERED? CAN BE DELETED NO?
public class FakeupCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.getName().equals("OGNamess") || sender.getName().equals("MrChestyVince") || sender.getName().equals("Baconerd") || sender.hasPermission("lc.fakeup") || sender.getName().equals("Designercop")) {
            if (args.length == 0) {
                sender.sendMessage(Utils.chat("&cPlease include player and rank!"));
            } else if (args.length == 1) {
                sender.sendMessage(Utils.chat("&cPlease include a rank!"));
            } else if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Utils.chat("&cPlayer not found!"));
                } else {
                    if (args[1].equalsIgnoreCase("II")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&2&l" + target.getName() + " &r&ahas ranked up to &c"));
                    } else if (args[1].equalsIgnoreCase("III")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&2&l" + target.getName() + " &r&ahas ranked up to &d"));
                    } else if (args[1].equalsIgnoreCase("IV")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&2&l" + target.getName() + " &r&ahas ranked up to &e"));
                    } else if (args[1].equalsIgnoreCase("V")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&2&l" + target.getName() + " &r&ahas ranked up to &6"));
                    } else if (args[1].equalsIgnoreCase("VI")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&2&l" + target.getName() + " &r&ahas ranked up to &f"));
                    } else if (args[1].equalsIgnoreCase("VII")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&2&l" + target.getName() + " &r&ahas ranked up to &2"));
                    } else if (args[1].equalsIgnoreCase("VIII")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&4&l" + target.getName() + " &r&chas ranked up to &a"));
                    } else if (args[1].equalsIgnoreCase("IX")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&4&l" + target.getName() + " &r&chas ranked up to &5"));
                    } else if (args[1].equalsIgnoreCase("X")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&4&l" + target.getName() + " &r&chas ranked up to &0"));
                    } else if (args[1].equalsIgnoreCase("XI")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&4&l" + target.getName() + " &r&chas ranked up to &4"));
                    } else if (args[1].equalsIgnoreCase("XII")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&3&l" + target.getName() + " &r&ahas ranked up to &8"));
                    } else if (args[1].equalsIgnoreCase("XIII")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&3&l" + target.getName() + " &r&ahas ranked up to &9"));
                    } else if (args[1].equalsIgnoreCase("XIV")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&6&l" + target.getName() + " &r&ehas ranked up to &3"));
                    } else if (args[1].equalsIgnoreCase("XV")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&6&l" + target.getName() + " &r&ehas ranked up to &7"));
                    } else if (args[1].equalsIgnoreCase("XVI")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&6&l" + target.getName() + " &r&ehas ranked up to &b"));
                    } else if (args[1].equalsIgnoreCase("XVII")) {
                        Bukkit.broadcastMessage(Utils.chat("&d&m---&5&l" + target.getName() + " &r&dhas ranked up to &0"));
                    } else {
                        sender.sendMessage(Utils.chat("&cRank not found!"));
                        return true;
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!args[1].equalsIgnoreCase("XVII")) {
                            p.playSound(p.getLocation(), Sound.CAT_MEOW, 1.0F, 1.43F);
                            continue;
                        }
                        p.playSound(p.getLocation(), Sound.ENDERDRAGON_DEATH, 1.0F, 1.0F);
                    }
                }
            } else {
                sender.sendMessage(Utils.chat("&cInvalid syntax!"));
            }
        } else {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
        }
        return true;
    }
}