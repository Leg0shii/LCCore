package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.LocationHelper;
import de.legoshi.lccore.util.MusicHelper;
import de.legoshi.lccore.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CheckPointListener implements Listener {

    private final FileConfiguration config = Linkcraft.getPlugin().getConfig();
    private final File pluginFolder = Linkcraft.getPlugin().getPluginFolder();

    public ItemStack getCPItem() {
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&bReturn to CP"));
        item.setItemMeta(itemMeta);
        return item;
    }

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
    public void onPlayerInteract(PlayerInteractEvent e) {
        String message_name = this.config.getString("message-name");
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock != null && (clickedBlock.getType() == Material.WALL_SIGN || clickedBlock.getType() == Material.SIGN_POST) && e.getAction() == Action.RIGHT_CLICK_BLOCK && isAParkourSign(e.getClickedBlock())) {
            Double[] possibleYValues;
            Location location;
            if (!e.getPlayer().hasPermission("pkcp.signs") || e.getPlayer().hasPermission("lc.pro")) {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have the permission to click this sign!"));
                return;
            }
            String fileName = e.getClickedBlock().getWorld().getName() + "_" + e.getClickedBlock().getX() + "_" + e.getClickedBlock().getY() + "_" + e.getClickedBlock().getZ() + ".yml";
            File file = new File(this.pluginFolder.getAbsolutePath() + File.separator + "checkpoint_signs", fileName);
            YamlConfiguration yamlConfiguration1 = YamlConfiguration.loadConfiguration(file);
            String playerFileName = e.getPlayer().getUniqueId().toString() + ".yml";
            File playerFile = new File(this.pluginFolder.getAbsolutePath() + File.separator + "player_checkpoint_data", playerFileName);
            if (!playerFile.exists())
                try {
                    playerFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            YamlConfiguration yamlConfiguration2 = YamlConfiguration.loadConfiguration(playerFile);
            String name = yamlConfiguration1.getString("Name");
            String type = yamlConfiguration1.getString("Type");
            switch (type) {
                case "ground":
                    possibleYValues = Utils.possBlockYValues();
                    if (e.getPlayer().getVelocity().getY() == -0.0784000015258789D && Arrays.asList(possibleYValues).contains(e.getPlayer().getLocation().getY() % 1.0D)) {
                        yamlConfiguration2.set("Checkpoints." + name + ".Location", LocationHelper.getStringFromLocation(e.getPlayer().getLocation()));
                        break;
                    }
                    e.getPlayer().sendRawMessage(ChatColor.translateAlternateColorCodes('&', message_name + " &cYou have to be on the ground to click this checkpoint!"));
                    return;
                case "direct":
                    yamlConfiguration2.set("Checkpoints." + name + ".Location", LocationHelper.getStringFromLocation(e.getPlayer().getLocation()));
                    break;
                default:
                    location = e.getClickedBlock().getLocation();
                    location.setX(location.getX() + 0.5D);
                    location.setZ(location.getZ() + 0.5D);
                    yamlConfiguration2.set("Checkpoints." + name + ".Location", LocationHelper.getStringFromLocation(location));
                    break;
            }
            yamlConfiguration2.set("Worlds." + e.getPlayer().getWorld().getName(), name);
            try {
                yamlConfiguration2.save(playerFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            MusicHelper.playMusic(e.getPlayer());
            e.getPlayer().sendRawMessage(ChatColor.translateAlternateColorCodes('&', message_name + " &aYou got the checkpoint!"));
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

    public boolean isAParkourSign(Block block) {
        if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
            String fileName = block.getWorld().getName() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + ".yml";
            String filePath = this.pluginFolder.getAbsolutePath() + File.separator + "checkpoint_signs" + File.separator + fileName;
            File file = new File(filePath);
            return file.exists();
        }
        return false;
    }
}
