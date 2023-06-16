package de.legoshi.lcpractice.command;

import de.legoshi.lcpractice.LCPractice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final LCPractice plugin;

    public ReloadCommand(LCPractice plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.reloadConfig();
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aReloaded the config!"));
        } else {
            Bukkit.broadcastMessage("Reloaded the config!");
        }
        return true;
    }
}

