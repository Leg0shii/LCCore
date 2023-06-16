package de.legoshi.lcpractice;

import de.legoshi.lcpractice.command.PracticeCommand;
import de.legoshi.lcpractice.command.ReloadCommand;
import de.legoshi.lcpractice.command.TestCommand;
import de.legoshi.lcpractice.command.UnpracticeCommand;
import de.legoshi.lcpractice.helper.ConfigAccessor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LCPractice extends JavaPlugin {

    public LuckPerms luckPerms;
    public ConfigAccessor playerdataConfigAccessor = new ConfigAccessor(this, "playerdata.yml");
    private final FileConfiguration config = getConfig();

    public void onEnable() {
        loadLuckPerms();
        saveDefaultConfig();
        this.playerdataConfigAccessor.saveDefaultConfig();
        getCommand("practice").setExecutor(new PracticeCommand(this));
        getCommand("unpractice").setExecutor(new UnpracticeCommand(this));
        getCommand("pkpracreload").setExecutor(new ReloadCommand(this));
        getCommand("test").setExecutor(new TestCommand());
        getServer().getPluginManager().registerEvents(new PkPracListener(this), (Plugin) this);
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
        Material material;
        String returnItemId = this.config.getString("return-item-id");
        String returnItemName = this.config.getString("return-item-name");
        int materialData = 0;
        String[] obj = returnItemId.split(":");
        if (obj.length == 2) {
            try {
                material = Material.matchMaterial(obj[0]);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("The material you used in config.yml does not exist!");
                return null;
            }
            try {
                materialData = Integer.parseInt(obj[1]);
            } catch (NumberFormatException e) {
                Bukkit.getConsoleSender().sendMessage("The data of the material you used in config.yml is not a number!");
                return null;
            }
        } else {
            try {
                material = Material.matchMaterial(returnItemId);
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("The material you used in config.yml does not exist!");
                return null;
            }
        }
        if (material == null) {
            Bukkit.getConsoleSender().sendMessage("The material you used in config.yml does not exist!");
            return null;
        }
        ItemStack item = new ItemStack(material, 1);
        item.setDurability((short) materialData);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', returnItemName));
        item.setItemMeta(itemMeta);
        return item;
    }
}

