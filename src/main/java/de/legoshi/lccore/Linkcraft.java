package de.legoshi.lccore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.legoshi.lccore.inject.LinkcraftModule;
import de.legoshi.lccore.manager.CommandManager;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.ListenerManager;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.papi.PlaceHolderAPI;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.MapUpdater;
import de.legoshi.lccore.util.Utils;
import fr.minuskube.inv.InventoryManager;
import me.clip.deluxetags.DeluxeTag;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Injector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Linkcraft extends JavaPlugin {

    public static Linkcraft instance;

    public final Map<String, String> saveCreations = new HashMap<>();
    public final Map<String, OfflinePlayer> playerMap = new HashMap<>();

    public static Economy economy = null;
    private static Chat chat = null;

    public LuckPerms luckPerms;
    public final ConfigAccessor playerConfig = new ConfigAccessor(this, "playerdata.yml");
    public final ConfigAccessor lockdownConfig = new ConfigAccessor(this, "lockdown.yml");
    public final ConfigAccessor mapsConfig = new ConfigAccessor(this, "maps.yml");
    public InventoryManager im;
    private final FileConfiguration config = getConfig();
    public ProtocolManager protocolManager;

    public void onEnable() {
        instance = this;

        loadDependencies();
        Injector injector = Injector.create(new LinkcraftModule(this));
        new ConfigManager(this).loadConfigs();
        new CommandManager(this, injector).registerCommands();
        new MapManager(this);
        try {
            MapUpdater.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        im = new InventoryManager(this);
        im.init();
        injector.getInstance(ListenerManager.class).registerEvents();


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

        protocolManager = ProtocolLibrary.getProtocolManager();
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
        for(Player p : Bukkit.getOnlinePlayers()) {
            ConfigAccessor playerData = new ConfigAccessor(this, getPlayerdataFolder(), p.getUniqueId() + ".yml");
            FileConfiguration playerDataConfig = playerData.getConfig();
            playerDataConfig.set("lastlocation", Utils.getStringFromLocation(p.getLocation()));
            playerDataConfig.set("jumps", p.getStatistic(Statistic.JUMP));
            playerDataConfig.set("tags", DeluxeTag.getAvailableTagIdentifiers(p));
            playerData.saveConfig();
        }

        this.playerConfig.saveConfig();
        this.lockdownConfig.saveConfig();
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

    public File getPluginFolder() {
        return getDataFolder();
    }
}

