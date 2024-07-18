package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CheckPointListener implements Listener {

    private final FileConfiguration config = Linkcraft.getPlugin().getConfig();
    private final File pluginFolder = Linkcraft.getPlugin().getPluginFolder();

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        String[] validSignTypes = { "ground", "direct", "here" };
        if (Arrays.asList(validSignTypes).contains(e.getLine(0))) {
            String signType = e.getLine(0).trim();
            createSign(e);
            switch (signType) {
                case "ground":
                    signType = "&a&l(&2&lGround&a&l)";
                    break;
                case "direct":
                    signType = "&d&l(&5&lDirect&d&l)";
                    break;
                case "here":
                    signType = "&9&l(&1&lHere&9&l)";
                    break;
                default:
                    signType = "&4&lDNE!";
                    break;
            }
            String sign_name = this.config.getString("sign-name");
            String courseName = e.getLine(1);
            e.setLine(0, ChatColor.translateAlternateColorCodes('&', sign_name));
            e.setLine(1, ChatColor.translateAlternateColorCodes('&', "&8checkpoint"));
            e.setLine(2, "[" + courseName + "]");
            e.setLine(3, ChatColor.translateAlternateColorCodes('&', signType));
            e.getPlayer().sendRawMessage("Created a new parkour sign!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST) {
            String fileName = e.getBlock().getWorld().getName() + "_" + e.getBlock().getX() + "_" + e.getBlock().getY() + "_" + e.getBlock().getZ() + ".yml";
            String filePath = this.pluginFolder.getAbsolutePath() + File.separator + "checkpoint_signs" + File.separator + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    e.getPlayer().sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&aSuccessfully deleted the Parkour Sign!"));
                } else {
                    e.getPlayer().sendRawMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not delete the Parkour Sign!"));
                }
            }
        }
    }

    public void createSign(SignChangeEvent e) {
        String fileName = e.getBlock().getWorld().getName() + "_" + e.getBlock().getX() + "_" + e.getBlock().getY() + "_" + e.getBlock().getZ() + ".yml";
        File file = new File(this.pluginFolder.getAbsolutePath() + File.separator + "checkpoint_signs", fileName);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(this.pluginFolder.getAbsolutePath() + File.separator + "checkpoint_signs", fileName));
        yamlConfiguration.addDefault("Name", e.getLine(1));
        yamlConfiguration.addDefault("Location", LocationHelper.getStringFromLocation(e.getBlock().getLocation()));
        yamlConfiguration.addDefault("Type", e.getLine(0));
        yamlConfiguration.options().copyDefaults(true);
        try {
            yamlConfiguration.save(this.pluginFolder.getAbsolutePath() + File.separator + "checkpoint_signs" + File.separator + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
