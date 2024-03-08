package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.menu.LCMap;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import org.bukkit.BanList;
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

    private final static String parkour = "§6«§r§6§lPARKOUR§r§6»§r";

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
        }


        //LCMap lcMap = MapManager.mapMap.get(map);

//        if(requiresBan(mapSection, lcMap)) {
//            ban(offlinePlayer);
//            preCompletion(mapsSection, lcMap);
//        }
//        else
        if(mapSection == null) {
            mapSection = mapsSection.createSection(map);
            mapSection.set("firstcompletion", obj);
            mapSection.set("completions", 1);
            mapSection.set("lastcompletion", obj);
            sender.sendMessage(ChatColor.GREEN + "Added first completion of " + map + " for " + args[1]);
        } else {
            int completions = mapSection.getInt("completions");
            String first = mapSection.getString("firstcompletion");
            if(first == null || first.isEmpty()) {
                mapSection.set("firstcompletion", obj);
            }
            mapSection.set("completions", completions + 1);
            mapSection.set("lastcompletion", obj);
            sender.sendMessage(ChatColor.GREEN + "Added new completion of " + map + " for " + args[1]);
        }

        Player player = Bukkit.getPlayer(offlinePlayer.getName());

        if(player != null && mapSection != null && args.length == 2) {
            //giveRewards(player, lcMap);
        }

        playerConfigAccessor.saveConfig();

        return true;
    }

    private boolean requiresBan(ConfigurationSection mapSection, LCMap map) {
        return mapSection == null && map.star_rating >= 6;
    }

    private void ban(OfflinePlayer offlinePlayer) {
        Bukkit.getBanList(BanList.Type.NAME).addBan(offlinePlayer.getName(), "You beat a hard map, notify staff!", null, "map completion");
        Player player = Bukkit.getPlayer(offlinePlayer.getName());
        if(player != null && player.isOnline()) {
            player.kickPlayer("You beat a hard map, notify staff!");
        }
    }

    private void preCompletion(ConfigurationSection mapsSection, LCMap map) {
        ConfigurationSection mapSection = mapsSection.createSection(map.id);
        mapSection.set("firstcompletion", "");
        mapSection.set("completions", 0);
        mapSection.set("lastcompletion", "");
    }

    private void giveRewards(Player player, LCMap map) {
        String warp = "spawn"; // = map.warp != null ? map.warp : "spawn"
        serverCommand("warp " + warp + " " + player.getName());

        if(map.pp > 0) {
            serverCommand("eco give " + player.getName() + " " + map.pp);
        }

        if(map.star_rating >= 7) {
            Bukkit.broadcastMessage(parkour + " §a" + player.getName() + " §r§7" + "has finished §r" + Utils.chat(map.name) + "§r§7! " + "§r§e§l" + map.star_rating + "*");
        } else if(map.star_rating >= 3.5) {
            Bukkit.broadcastMessage(parkour + " §a" + player.getName() + " §r§7" + "has finished §r" + Utils.chat(map.name) + "§r§7! " + "§r§e" + map.star_rating + "*");
        } else {
            player.sendMessage(parkour + " §7You have just completed §r" + Utils.chat(map.name) + "§r§7! " + "§r§e" + map.star_rating + "*");
        }
    }

    private void serverCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }
}
