package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.menu.LCMap;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Date;

public class CompleteCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.complete")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
            return true;
        }

        if(args == null || args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /complete <map> <player>");
            return true;
        }

        String map = args[0];

        if(!MapManager.mapExists(map)) {
            sender.sendMessage( ChatColor.RED + "That map doesn't exist, perhaps the name was changed?");
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

        if(offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "That player does not exist!");
            return true;
        }

        ConfigAccessor playerConfigAccessor = Utils.getPlayerConfig(offlinePlayer);
        FileConfiguration playerConfig = playerConfigAccessor.getConfig();
        ConfigurationSection mapsSection = playerConfig.getConfigurationSection("maps");
        if(mapsSection == null) {
            playerConfig.createSection("maps");
            mapsSection = playerConfig.getConfigurationSection("maps");
        }

        ConfigurationSection mapSection = mapsSection.getConfigurationSection(map);


        Date current = new Date();
        Object obj = current;

        if(args.length > 2) {
            obj = "N/A";
        } else {
            LCMap lcMap = MapManager.mapMap.get(map);
//            if(lcMap.pp > 0) {
//                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "eco give " + offlinePlayer.getName() + " " + lcMap.pp);
//            }
        }

        if(mapSection == null) {
            mapSection = mapsSection.createSection(map);
            mapSection.set("firstcompletion", obj);
            mapSection.set("completions", 1);
            mapSection.set("lastcompletion", obj);
            sender.sendMessage(ChatColor.GREEN + "Added first completion of " + map + " for " + args[1]);
        } else {
            int completions = mapSection.getInt("completions");
            mapSection.set("completions", completions + 1);
            mapSection.set("lastcompletion", obj);
            sender.sendMessage(ChatColor.GREEN + "Added new completion of " + map + " for " + args[1]);
        }


        playerConfigAccessor.saveConfig();

        return true;
    }
}
