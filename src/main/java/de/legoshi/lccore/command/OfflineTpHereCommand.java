package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class OfflineTpHereCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (!sender.hasPermission("lc.otphere")) {
            sender.sendMessage(ChatColor.RED + "Insufficient Permission!");
            return true;
        }

        if (args == null || args.length < 1) {
            sender.sendMessage(ChatColor.RED + "This command requires a player name!");
            return true;
        }

        if(Bukkit.getPlayer(args[0]) != null) {
            sender.sendMessage(ChatColor.RED + "That player is currently online!");
            return true;
        }

        OfflinePlayer toTp = Bukkit.getOfflinePlayer(args[0]);

        if(!toTp.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "That player does not exist!");
            return true;
        }

        Player player = (Player)sender;
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getInstance(), Linkcraft.getInstance().getPlayerdataFolder(), toTp.getUniqueId().toString() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();

        String locString = Utils.getStringFromLocation(player.getLocation());
        playerDataConfig.set("lastlocation", locString);
        playerData.saveConfig();
        Linkcraft.getInstance().playerdataConfigAccessor.getConfig().set(toTp.getUniqueId().toString(), null);

        player.sendMessage(ChatColor.GREEN + "Updated " + args[0] + "'s location to: " + locString);

        return true;
    }
}
