package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CheckpointCommand implements CommandExecutor {

    private final Linkcraft plugin;
    private final File pluginFolder;

    public CheckpointCommand(Linkcraft plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();
            File file = new File(this.pluginFolder.getAbsolutePath() + File.separator + "players" + File.separator + uuid + ".yml");
            if (!file.exists()) {
                player.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have a checkpoint to go to! (You don't have a player file)"));
                return true;
            }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            String world = player.getWorld().getName();
            String mapName = yamlConfiguration.getString("Worlds." + world);
            String locationString = yamlConfiguration.getString("Checkpoints." + mapName + ".Location");
            Location location = LocationHelper.getLocationFromString(locationString);
            if (location == null) {
                player.sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have a checkpoint to go to!"));
                return true;
            }
            player.teleport(location);
        } else {
            this.plugin.getLogger().info("You can only send this command as a player!");
        }
        return true;
    }
}