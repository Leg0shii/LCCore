package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.LocationHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SendToMapCommand implements CommandExecutor {

    private final String pluginFolderPath;

    public SendToMapCommand(String pluginFolderPath) {
        this.pluginFolderPath = pluginFolderPath;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2)
            return false;
        Player senderPlayer = (Player) sender;
        String playerName = args[0];
        String map = args[1];
        for (int i = 2; i < args.length; i++)
            map = map + " " + args[i];
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            senderPlayer.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player cannot be found!"));
            return true;
        }
        String uuid = player.getUniqueId().toString();
        File file = new File(this.pluginFolderPath + File.separator + "player_checkpoint_data" + File.separator + uuid + ".yml");
        if (!file.exists()) {
            senderPlayer.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player cannot go to that map! (player file dne)"));
            return true;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String locationString = yamlConfiguration.getString("Checkpoints." + map + ".Location");
        if (locationString == null) {
            senderPlayer.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player cannot go to that map! (cp dne)"));
            return true;
        }
        Location location = LocationHelper.getLocationFromString(locationString);
        player.teleport(location);
        String world = player.getWorld().getName().toString();
        yamlConfiguration.set("Worlds." + world, map);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}