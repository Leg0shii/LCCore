package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class CheckpointDeleteCommand implements CommandExecutor {

    private final Linkcraft plugin;
    private final String pluginFolderPath;

    public CheckpointDeleteCommand(Linkcraft plugin) {
        this.plugin = plugin;
        this.pluginFolderPath = plugin.getPluginFolder().getAbsolutePath();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2)
            return false;
        String playerName = args[0];
        StringBuilder map = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++)
            map.append(" ").append(args[i]);
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player cannot be found!"));
            return true;
        }
        String uuid = player.getUniqueId().toString();
        File file = new File(this.pluginFolderPath + File.separator + "player_checkpoint_data" + File.separator + uuid + ".yml");
        if (!file.exists()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player's checkpoint can't be removed! (file dne)"));
            return true;
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        if (map.toString().equals("***")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDeleted all of their saves!"));
            file.delete();
            return true;
        }

        if (map.toString().equals("*bonus*")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDeleted all of their bonus checkpoints!"));
            Set<String> cps = yamlConfiguration.getConfigurationSection("Checkpoints").getKeys(false);
            for (String cp : cps) {
                if (cp.contains("CP#")) {
                    yamlConfiguration.set("Checkpoints." + cp, null);
                }
            }
            try {
                yamlConfiguration.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        String locationString = yamlConfiguration.getString("Checkpoints." + map + ".Location");
        if (locationString == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis player doesn't have this map!"));
            return true;
        }
        yamlConfiguration.set("Checkpoints." + map, null);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("message-name") + " &aThe player's checkpoint was successfully deleted!"));
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<String> worlds = yamlConfiguration.getConfigurationSection("Worlds").getKeys(false);
        Object[] worldArray = worlds.toArray();
        for (int j = 0; j < (worlds.toArray()).length; j++) {
            if (Objects.equals(yamlConfiguration.getString("Worlds." + worldArray[j]), map.toString()))
                yamlConfiguration.set("Worlds." + worldArray[j], null);
        }
        return true;
    }
}