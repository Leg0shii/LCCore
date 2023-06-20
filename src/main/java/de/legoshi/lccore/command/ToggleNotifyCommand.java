package de.legoshi.lccore.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// PARTLY REFACTORED
public class ToggleNotifyCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lc.notifications") || !(sender instanceof org.bukkit.entity.Player)) return true;

        if (sender.hasPermission("maxbans.notify")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + sender.getName() + " permission set maxbans.notify false");
            sender.sendMessage(ChatColor.RED + "Turned off maxbans notify!");
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + sender.getName() + " permission set maxbans.notify true");
            sender.sendMessage(ChatColor.GREEN + "Turned on maxbans notify!");
        }
        return true;
    }

}