package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class OfflineTpCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        if (!sender.hasPermission("lc.otp")) {
            sender.sendMessage(ChatColor.RED + "Insufficient Permission!");
            return true;
        }

        if (args == null || args.length < 1) {
            sender.sendMessage(ChatColor.RED + "This command requires a player name!");
            return true;
        }

        OfflinePlayer toTpTo = Bukkit.getOfflinePlayer(args[0]);

        if(!toTpTo.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "That player does not exist!");
            return true;
        }

        Player player = (Player)sender;
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), toTpTo.getUniqueId().toString() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();

        Location location =  Utils.getLocationFromString(playerDataConfig.getString("lastlocation"));
        player.teleport(location);

        player.sendMessage(ChatColor.GREEN + "Teleport to " + args[0] + "'s last logout location");

        return true;
    }
}
