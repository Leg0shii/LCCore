package de.legoshi.lcpractice;

import de.legoshi.lcpractice.command.PracticeCommand;
import de.legoshi.lcpractice.command.ReloadCommand;
import de.legoshi.lcpractice.command.UnpracticeCommand;
import de.legoshi.lcpractice.helper.ConfigAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LCPractice extends JavaPlugin {

    public ConfigAccessor playerdataConfigAccessor = new ConfigAccessor(this, "playerdata.yml");
    private final FileConfiguration config = getConfig();

    public void onEnable() {
        saveDefaultConfig();
        this.playerdataConfigAccessor.saveDefaultConfig();
        getCommand("practice").setExecutor(new PracticeCommand(this));
        getCommand("unpractice").setExecutor(new UnpracticeCommand(this));
        getCommand("pkpracreload").setExecutor(new ReloadCommand(this));
        getServer().getPluginManager().registerEvents(new PkPracListener(this), (Plugin) this);
    }

    public void onDisable() {
        this.playerdataConfigAccessor.saveConfig();
    }

    public ItemStack getReturnItem() {
        Material mat;
        String returnItemId = this.config.getString("return-item-id");
        String returnItemName = this.config.getString("return-item-name");
        int data = 0;
        String[] obj = returnItemId.split(":");
        if (obj.length == 2) {
            try {
                mat = Material.matchMaterial(obj[0]);
            } catch (Exception var10) {
                Bukkit.getConsoleSender().sendMessage("The material you used in config.yml does not exist!");
                return null;
            }
            try {
                data = Integer.valueOf(obj[1]).intValue();
            } catch (NumberFormatException var9) {
                Bukkit.getConsoleSender().sendMessage("The data of the material you used in config.yml is not a number!");
                return null;
            }
        } else {
            try {
                mat = Material.matchMaterial(returnItemId);
            } catch (Exception var8) {
                Bukkit.getConsoleSender().sendMessage("The material you used in config.yml does not exist!");
                return null;
            }
        }
        if (mat == null) {
            Bukkit.getConsoleSender().sendMessage("The material you used in config.yml does not exist!");
            return null;
        }
        ItemStack item = new ItemStack(mat, 1);
        item.setDurability((short) data);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', returnItemName));
        item.setItemMeta(itemMeta);
        return item;
    }
}

