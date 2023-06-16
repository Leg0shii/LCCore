package de.legoshi.lcpractice;

import de.legoshi.lcpractice.util.ConfigAccessor;
import de.legoshi.lcpractice.manager.CommandManager;
import de.legoshi.lcpractice.manager.ConfigManager;
import de.legoshi.lcpractice.util.Constants;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class LCPractice extends JavaPlugin {

    public LuckPerms luckPerms;
    public ConfigAccessor playerdataConfigAccessor = new ConfigAccessor(this, "playerdata.yml");
    private final FileConfiguration config = getConfig();

    public void onEnable() {
        loadLuckPerms();

        new ConfigManager(this).loadConfigs();
        new CommandManager(this).registerCommands();

        getServer().getPluginManager().registerEvents(new PkPracListener(this), this);
    }

    private void loadLuckPerms() {
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            Bukkit.getConsoleSender().sendMessage("Couldn't load LuckPerms API! Shutting down Practice Plugin");
            Bukkit.shutdown();
        }
    }

    public void onDisable() {
        this.playerdataConfigAccessor.saveConfig();
    }

    public ItemStack getReturnItem() {
        String returnItemId = this.config.getString(Constants.RETURN_ITEM_ID);
        String returnItemName = this.config.getString(Constants.RETURN_ITEM_NAME);

        String[] obj = returnItemId.split(":");
        Material material = getMaterial(obj[0]);
        int materialData = getMaterialData(obj);

        ItemStack item = new ItemStack(material, 1);
        item.setDurability((short) materialData);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', returnItemName));
        item.setItemMeta(itemMeta);
        return item;
    }

    private Material getMaterial(String materialName) {
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("The material " + materialName + " does not exist!");
        }
        return material;
    }

    private int getMaterialData(String[] materialData) {
        if (materialData.length == 2) {
            try {
                return Integer.parseInt(materialData[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The data of the material " + materialData[0] + " is not a number!");
            }
        }
        return 0;
    }
}

