package de.legoshi.lcpractice.command;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// PARTLY REFACTORED
public class ReloadCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public ReloadCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (!p.hasPermission("ps.reload")) {
                p.sendMessage(Utils.chat("&cYou don't have permission to do this command! (ps.reload)"));
                return true;
            }

            this.plugin.reloadConfig();
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lParkour&b&lSaves &8&aReloaded the config!"));
        } else {
            this.plugin.reloadConfig();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lParkour&b&lSaves &8\\u00BB &aReloaded the config"));
        }
        return true;
    }

}