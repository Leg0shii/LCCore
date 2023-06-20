package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.LocationHelper;
import de.legoshi.lccore.util.MusicHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SaveCheckpointCommand implements CommandExecutor {

    private final Linkcraft plugin;
    private final File pluginFolder;

    public SaveCheckpointCommand(Linkcraft plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getPluginFolder();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String playerFileName = player.getUniqueId().toString() + ".yml";
            File playerFile = new File(this.pluginFolder.getAbsolutePath() + File.separator + "player_checkpoint_data", playerFileName);
            if (!playerFile.exists())
                try {
                    playerFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(playerFile);
            yamlConfiguration.set("Checkpoints.Location", LocationHelper.getStringFromLocation(player.getLocation()));
            yamlConfiguration.set("Worlds." + player.getWorld().getName(), "");
            try {
                yamlConfiguration.save(playerFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            FileConfiguration config = this.plugin.getConfig();
            MusicHelper.playMusic(player);
            String message_name = config.getString("message-name");
            player.sendRawMessage(ChatColor.translateAlternateColorCodes('&', message_name + " &aYou got the checkpoint!"));
        } else {
            this.plugin.getLogger().info("You can only send this command as a player!");
        }
        return true;
    }
}