package de.legoshi.lccore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.legoshi.lccore.inject.LinkcraftModule;
import de.legoshi.lccore.manager.*;
import de.legoshi.lccore.papi.PlaceHolderAPI;
import de.legoshi.lccore.service.Service;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Constants;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
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
import org.bukkit.scheduler.BukkitTask;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Linkcraft extends JavaPlugin {

    @Getter
    public static Linkcraft plugin;

    public final Map<String, String> saveCreations = new HashMap<>();
    public final Map<String, OfflinePlayer> playerMap = new HashMap<>();

    public static Economy economy = null;
    private static Chat chat = null;
    private Injector injector;

    public LuckPerms luckPerms;
    public final ConfigAccessor playerConfig = new ConfigAccessor(this, "playerdata.yml");
    public final ConfigAccessor lockdownConfig = new ConfigAccessor(this, "lockdown.yml");
    public final ConfigAccessor mapsConfig = new ConfigAccessor(this, "maps.yml");
    public final ConfigAccessor rankConfig = new ConfigAccessor(this, "rankdata.yml");
    public InventoryManager im;
    private final FileConfiguration config = getConfig();
    public ProtocolManager protocolManager;
    public ConfigManager configManager;

    @Inject private PlayerManager playerManager;
    @Inject private Service service;

    public void onEnable() {
        plugin = this;

        loadDependencies();
        injector = Injector.create(new LinkcraftModule(this));
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        new CommandManager(this, injector).registerCommands();
        new MapManager(this);

        // TODO: refactor inventory library
        im = new InventoryManager(this);
        im.init();

        injector.injectMembers(this);
        injector.getInstance(ListenerManager.class).registerEvents();
        service.start();
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
        PlayerManager playerManager = injector.getInstance(PlayerManager.class);
        for(Player p : Bukkit.getOnlinePlayers()) {
            playerManager.playerLeave(p);
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

    public File getPlayerdataFolder() {
        return new File(getDataFolder(), "playerdata");
    }

    public File getPluginFolder() {
        return getDataFolder();
    }

    public static void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public static BukkitTask asyncLater(Runnable runnable, Long ticks) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, ticks);
    }

    public static void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static void syncLater(Runnable runnable, Long ticks) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }
}

