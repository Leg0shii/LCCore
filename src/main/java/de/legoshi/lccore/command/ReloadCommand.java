package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// PARTLY REFACTORED
public class ReloadCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public ReloadCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("lc.reload")) {
            sender.sendMessage(ChatColor.RED + "Insufficient permission");
            return true;
        }
        plugin.configManager.loadConfigs();
        sender.sendMessage(ChatColor.GREEN + "[LC] Reload complete!");
        return true;
    }

}