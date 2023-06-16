package de.legoshi.lcpractice;

import de.legoshi.lcpractice.manager.ListenerManager;
import de.legoshi.lcpractice.papi.PlaceHolderAPI;
import de.legoshi.lcpractice.util.ConfigAccessor;
import de.legoshi.lcpractice.manager.CommandManager;
import de.legoshi.lcpractice.manager.ConfigManager;
import de.legoshi.lcpractice.util.Constants;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Linkcraft extends JavaPlugin {

    public static Linkcraft instance;

    public Map<String, String> saveCreations = new HashMap<>();
    public Map<String, OfflinePlayer> playerMap = new HashMap<>();

    public static Economy economy = null;
    private static Chat chat = null;

    public LuckPerms luckPerms;
    public ConfigAccessor playerdataConfigAccessor = new ConfigAccessor(this, "playerdata.yml");
    private final FileConfiguration config = getConfig();

    public void onEnable() {
        instance = this;

        loadDependencies();
        new ConfigManager(this).loadConfigs();
        new CommandManager(this).registerCommands();
        new ListenerManager(this).registerEvents();
    }

    private void loadDependencies() {
        loadLuckPerms();

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceHolderAPI().register();
        }

        if(!setupEconomy()) {
            Bukkit.shutdown();
        } else {
            setupChat();
        }
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

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
            economy = economyProvider.getProvider();
        return (economy != null);
    }

    public Economy getEconomy() {
        return economy;
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
    }

    public Chat getChat() {
        return chat;
    }

    public static Linkcraft getInstance() {
        return instance;
    }

    public File getPlayerdataFolder() {
        return new File(getDataFolder(), "playerdata");
    }
}

