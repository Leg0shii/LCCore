package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.player.display.*;
import de.legoshi.lccore.util.ColorHelper;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.ConfigWriter;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageType;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    public static Map<String, RankDTO> ranksDisplay = new HashMap<>();
    public static Map<String, BonusDTO> bonusDisplay = new HashMap<>();
    public static Map<String, WolfDTO> wolfDisplay = new HashMap<>();
    public static Map<String, MazeDTO> mazeDisplay = new HashMap<>();
    public static Map<String, StarDTO> starDisplay = new HashMap<>();
    public static Map<String, StaffDTO> staffDisplay = new HashMap<>();
    public static Map<String, String> modIdBlacklist = new HashMap<>();
    public static final Map<Message, String> messages = new HashMap<>();
    public static final HashSet<String> keys = new HashSet<>();
    public static String host;
    public static String username;
    public static String password;
    public static int port;
    public static String database;
    @Inject private MapManager mapManager;

    public void init() {
        loadConfigs(true);
    }

    public void loadConfigs(boolean first) {
        Linkcraft plugin = Linkcraft.getPlugin();
        plugin.saveDefaultConfig();
        plugin.playerConfig.saveDefaultConfig();
        plugin.lockdownConfig.saveDefaultConfig();
        plugin.mapsConfig.saveDefaultConfig();
        plugin.rankConfig.saveDefaultConfig();
        plugin.dbConfig.saveDefaultConfig();
        plugin.modBlacklist.saveDefaultConfig();

        File playerData = new File(plugin.getDataFolder(), "playerdata");
        if (!playerData.exists()) playerData.mkdir();

        File signs = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "checkpoint_signs");
        if (!signs.exists()) signs.mkdir();

        File players = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "player_checkpoint_data");
        if (!players.exists()) players.mkdir();

        ColorHelper.load();
        loadDbConfig();
        loadRankDataIntoMemory();
        loadModBlacklistConfig();

        try {
            mapManager.updateMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadMessages();
        if(!first) {
            reloadPlayerCosmetics();
        }
    }

    private void loadModBlacklistConfig() {
        modIdBlacklist.clear();
        FileConfiguration blacklistData = new ConfigAccessor(Linkcraft.getPlugin(), "blacklisted-forge-mods.yml").getConfig();
        List<Map<String, String>> blacklisted = (List<Map<String, String>>)blacklistData.get("blacklisted");

        for(Map<String, String> entry : blacklisted) {
            modIdBlacklist.put(entry.get("modid"), entry.get("message"));
        }
    }

    private void loadDbConfig() {
        FileConfiguration dbData = new ConfigAccessor(Linkcraft.getPlugin(), "database.yml").getConfig();

        host = dbData.getString("host");
        database = dbData.getString("database");
        port = dbData.getInt("port");
        username = dbData.getString("username");
        password = dbData.getString("password");
    }

    private void reloadPlayerCosmetics() {
        PlayerManager playerManager = Linkcraft.getPlugin().getInjector().getInstance(PlayerManager.class);
        for(Player player : Bukkit.getOnlinePlayers()) {
            playerManager.updatePlayer(player);
        }
    }

    private void loadMessages() {
        File file = new File("./plugins/LCCore/" + "messages.yml");
        boolean valid = true;

        file.delete();

        ConfigWriter lcMessagesConfig = new ConfigWriter("./plugins/LCCore/", "messages.yml");

        try {
            if (!file.exists()) {
                valid = file.createNewFile();
            }
        } catch (IOException e) {
            MessageUtil.log(Message.CONFIG_LOADING_ERR, true, "messages config file");
        }

        if (valid) {
            HashMap<MessageType, HashSet<Message>> toAdd = new HashMap<>();

            for(Message message : Message.values()) {
                toAdd.computeIfAbsent(message.type, (m) -> toAdd.put(m, new HashSet<>()));
                toAdd.get(message.type).add(message);
            }

            for(Map.Entry<MessageType, HashSet<Message>> entries : toAdd.entrySet()) {
                String parent = entries.getKey().name().toLowerCase() + ".";
                for(Message message : entries.getValue()) {
                    String msgName = message.name().toLowerCase();
                    if(lcMessagesConfig.getString(parent + msgName) == null || lcMessagesConfig.getString(parent + msgName).isEmpty()) {
                        lcMessagesConfig.setValue(parent + message.name().toLowerCase(), message.getMessage());
                    }
                }
            }
            lcMessagesConfig.save();
            cacheMessages(lcMessagesConfig);
        }
    }

    private void cacheMessages(ConfigWriter configWriter) {
        for(Message message : Message.values()) {
            String path = message.type.name().toLowerCase() + "." + message.name().toLowerCase();
            messages.put(message, configWriter.getString(path));
        }
    }

    private void loadRankDataIntoMemory() {
        ranksDisplay = new HashMap<>();
        bonusDisplay = new HashMap<>();
        wolfDisplay = new HashMap<>();
        starDisplay = new HashMap<>();
        mazeDisplay = new HashMap<>();
        staffDisplay = new HashMap<>();
        FileConfiguration rankData = new ConfigAccessor(Linkcraft.getPlugin(), "rankdata.yml").getConfig();
        loadRankSection(rankData.getConfigurationSection("ranks"));
        loadBonusSection(rankData.getConfigurationSection("bonus"));
        loadWolfSection(rankData.getConfigurationSection("wolf"));
        loadMazeSection(rankData.getConfigurationSection("maze"));
        loadStarSection(rankData.getConfigurationSection("star"));
        loadStaffSection(rankData.getConfigurationSection("staff"));

        addToSet(ranksDisplay, "group.");
        addToSet(bonusDisplay);
        addToSet(wolfDisplay, "group.");
        addToSet(mazeDisplay);
        addToSet(starDisplay);
        addToSet(staffDisplay, "group.");
    }

    private void addToSet(Map<String, ?> map, String toAdd) {
        for(String key : map.keySet()) {
            keys.add(toAdd + key.toLowerCase());
        }
    }

    private void addToSet(Map<String, ?> map) {
        addToSet(map, "");
    }

    private void loadRankSection(ConfigurationSection ranks) {
        if(ranks != null) {
            for(String key : ranks.getKeys(false)) {
                ConfigurationSection rank = ranks.getConfigurationSection(key);
                String display = rank.getString("display");
                String tabDisplay = rank.getString("tabDisplay");
                String colorCode = rank.getString("colorCode");
                int position = rank.getInt("position");
                ranksDisplay.put(key, new RankDTO(key, display, tabDisplay, colorCode, position));
            }
        }
    }

    private void loadBonusSection(ConfigurationSection bonuses) {
        if(bonuses != null) {
            for(String key : bonuses.getKeys(false)) {
                ConfigurationSection bonus = bonuses.getConfigurationSection(key);
                String display = bonus.getString("display");
                int position = bonus.getInt("position");
                bonusDisplay.put("prefix." + key, new BonusDTO("prefix." + key, display, position));
            }
        }
    }

    private void loadWolfSection(ConfigurationSection wolfs) {
        if(wolfs != null) {
            for(String key : wolfs.getKeys(false)) {
                ConfigurationSection wolf = wolfs.getConfigurationSection(key);
                String display = wolf.getString("display");
                int position = wolf.getInt("position");
                wolfDisplay.put(key, new WolfDTO(key, display, position));
            }
        }
    }

    private void loadMazeSection(ConfigurationSection mazes) {
        if(mazes != null) {
            for(String key : mazes.getKeys(false)) {
                ConfigurationSection maze = mazes.getConfigurationSection(key);
                String tabDisplay = maze.getString("tabDisplay");
                String display = maze.getString("display");
                int position = maze.getInt("position");
                mazeDisplay.put("maze." + key, new MazeDTO("maze." + key, tabDisplay, display, position));
            }
        }
    }

    private void loadStarSection(ConfigurationSection stars) {
        if(stars != null) {
            for(String key : stars.getKeys(false)) {
                ConfigurationSection star = stars.getConfigurationSection(key);
                String display = star.getString("display");
                int cost = star.getInt("cost");
                int position = star.getInt("position");
                starDisplay.put(key, new StarDTO(key, display, cost, position));
            }
        }
    }

    private void loadStaffSection(ConfigurationSection staffs) {
        if(staffs != null) {
            for(String key : staffs.getKeys(false)) {
                ConfigurationSection staff = staffs.getConfigurationSection(key);
                String display = staff.getString("display");
                int position = staff.getInt("position");
                staffDisplay.put(key, new StaffDTO(key, display, position));
            }
        }
    }
}