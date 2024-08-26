package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.player.display.*;
import de.legoshi.lccore.player.practice.PracticeItem;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageType;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    public static Map<String, RankDTO> ranksDisplay = new HashMap<>();
    public static Map<String, BonusDTO> bonusDisplay = new HashMap<>();
    public static Map<String, WolfDTO> wolfDisplay = new HashMap<>();
    public static Map<String, MazeDTO> mazeDisplay = new HashMap<>();
    public static Map<String, StarDTO> starDisplay = new HashMap<>();
    public static Map<String, StaffDTO> staffDisplay = new HashMap<>();
    public static Map<String, ChatColorDTO> chatColorsMap = new HashMap<>();
    public static Map<String, CommandShopDTO> commandShopMap = new HashMap<>();
    public static Map<String, String> modIdBlacklist = new HashMap<>();
    public static Map<String, PracticeItem> practiceItems = new HashMap<>();
    public static final Map<Message, String> messages = new HashMap<>();
    public static final HashSet<String> keys = new HashSet<>();
    public static String host;
    public static String username;
    public static String password;
    public static int port;
    public static String database;
    @Inject private MapManager mapManager;
    @Inject private AchievementManager achievementManager;

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
        plugin.achievementConfig.saveDefaultConfig();

        File playerData = new File(plugin.getDataFolder(), "playerdata");
        if (!playerData.exists()) playerData.mkdir();

        File signs = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "checkpoint_signs");
        if (!signs.exists()) signs.mkdir();

        File players = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "player_checkpoint_data");
        if (!players.exists()) players.mkdir();

        ColorHelper.load();
        loadDbConfig();
        loadLCDataIntoMemory();
        loadModBlacklistConfig();

        try {
            mapManager.updateMaps();
        } catch (Exception e) {
            e.printStackTrace();
        }

        achievementManager.loadAchievements();

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

    private void loadLCDataIntoMemory() {
        ranksDisplay = new HashMap<>();
        bonusDisplay = new HashMap<>();
        wolfDisplay = new HashMap<>();
        starDisplay = new HashMap<>();
        mazeDisplay = new HashMap<>();
        staffDisplay = new HashMap<>();
        chatColorsMap = new HashMap<>();
        commandShopMap = new HashMap<>();
        FileConfiguration lcData = new ConfigAccessor(Linkcraft.getPlugin(), "lcdata.yml").getConfig();
        loadRankSection(lcData.getConfigurationSection("ranks"));
        loadBonusSection(lcData.getConfigurationSection("bonus"));
        loadWolfSection(lcData.getConfigurationSection("wolf"));
        loadMazeSection(lcData.getConfigurationSection("maze"));
        loadStarSection(lcData.getConfigurationSection("star"));
        loadStaffSection(lcData.getConfigurationSection("staff"));
        loadChatColorSection(lcData.getConfigurationSection("chatcolor"));
        loadCommandShopSection(lcData.getConfigurationSection("commands"));
        loadPracticeItems();


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
                String color = star.getString("color");

                ItemStack itemObj;
                String item = star.getString("item");

                String[] itemData = item.split(":");
                if(itemData.length > 1) {
                    itemObj = new ItemStack(Integer.parseInt(itemData[0]), 1, Short.parseShort(itemData[1]));
                } else {
                    itemObj = new ItemStack(Integer.parseInt(itemData[0]));
                }

                int cost = star.getInt("cost");
                int position = star.getInt("position");
                starDisplay.put(key, new StarDTO(key, display, cost, position, itemObj, color));
            }
        }
    }

    private void loadChatColorSection(ConfigurationSection chatcolors) {
        if(chatcolors != null) {
            for(String key : chatcolors.getKeys(false)) {
                ConfigurationSection chatColor = chatcolors.getConfigurationSection(key);
                String code = chatColor.getString("code");

                ItemStack itemObj;
                String item = chatColor.getString("item");

                String[] itemData = item.split(":");
                if(itemData.length > 1) {
                    itemObj = new ItemStack(Integer.parseInt(itemData[0]), 1, Short.parseShort(itemData[1]));
                } else {
                    itemObj = new ItemStack(Integer.parseInt(itemData[0]));
                }

                int cost = chatColor.getInt("cost");
                int position = chatColor.getInt("position");
                chatColorsMap.put(key, new ChatColorDTO(key, code, cost, position, itemObj));
            }
        }
    }

    private void loadCommandShopSection(ConfigurationSection commands) {
        if(commands != null) {
            for(String key : commands.getKeys(false)) {
                ConfigurationSection command = commands.getConfigurationSection(key);
                String display = command.getString("display");
                String description = command.getString("description");
                List<String> permissionList = new ArrayList<>();
                String permission = command.getString("permission");

                String[] permissionData = permission.split(":");
                if(permissionData.length > 1) {
                    permissionList.addAll(Arrays.asList(permissionData));
                } else {
                    permissionList.add(permissionData[0]);
                }
                int cost = command.getInt("cost");
                int position = command.getInt("position");
                commandShopMap.put(key, new CommandShopDTO(key, display, description, permissionList, cost, position));
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

    private void loadPracticeItems() {
        PracticeItem defaultPracticeItem = new PracticeItem();
        defaultPracticeItem.setItemText(new GUIDescriptionBuilder().raw("§3§l【§b§lLC§3§l】-§4Return"));
        defaultPracticeItem.setItemMat(Material.SLIME_BALL);
        defaultPracticeItem.setPracticeMessage("§3§l【§b§lLinkCraft§3§l】- §aYou are on a practice mode, Do /unprac to stop.");
        defaultPracticeItem.setUnpracticeMessage("§3§l【§b§lLinkCraft§3§l】- §4You have stopped practicing.");
        defaultPracticeItem.setAlreadyPracticeMessage("§3§l【§b§lLinkCraft§3§l】- §4You are already in practice mode!");
        defaultPracticeItem.setNotInPracticeMessage("§3§l【§b§lLinkCraft§3§l】- §4You are not in a practice mode.");
        defaultPracticeItem.setActionBarMessage("§b§lYou are practicing!");
        defaultPracticeItem.setNotOnBlockMessage("§3§l【§b§lLinkCraft§3§l】- §4You must be on a block to use the practice system!");
        defaultPracticeItem.build();

        PracticeItem hpkPracticeItem = new PracticeItem();
        Audio hpkErrorAudio = new Audio(Sound.NOTE_BASS, 1, 1);
        hpkPracticeItem.setItemText(new GUIDescriptionBuilder().raw("§cBack to checkpoint §8[§7Right-click§8]")
                .raw("§7Return to your previous checkpoint.")
                .raw("§8Item ID: cp_returner"));
        hpkPracticeItem.setItemMat(Material.INK_SACK);
        hpkPracticeItem.setItemDamage((short)1);
        hpkPracticeItem.setPracticeMessage("§8[§2§l!§8] §7Enabled §bPractice Mode§7.");
        hpkPracticeItem.setUnpracticeMessage("§8[§3§l!§8] §7Disabled §bPractice Mode.");
        hpkPracticeItem.setNotOnBlockMessage("§8[§3§l!§8] §cYou have to be on ground.");
        hpkPracticeItem.setNotOnBlockAudio(hpkErrorAudio);
        hpkPracticeItem.setNotInPracticeMessage("§8[§4§l!§8] §cYou aren't in practice mode.");
        hpkPracticeItem.setNotInPracticeAudio(hpkErrorAudio);
        hpkPracticeItem.setAlreadyPracticeMessage("§8[§4§l!§8] §cYou are already in practice mode.");
        hpkPracticeItem.setAlreadyPracticeAudio(hpkErrorAudio);
        hpkPracticeItem.setActionBarMessage("§3You are in §bPractice Mode§3.");
        hpkPracticeItem.setUnpracticeAudio(new Audio(Sound.CLICK, 1, 1));
        hpkPracticeItem.setPracticeAudio(new Audio(Sound.CLICK, 1, 1));
        // Assumed
        hpkPracticeItem.setFullInventoryMessage("§8[§4§l!§8] §cYour inventory is full.");
        hpkPracticeItem.setFullInventoryAudio(hpkErrorAudio);
        hpkPracticeItem.build();
        PracticeItem pkuPracticeItem = new PracticeItem();
        pkuPracticeItem.setItemText(new GUIDescriptionBuilder().raw("§cReturn"));
        pkuPracticeItem.setItemMat(Material.FIREWORK);
        pkuPracticeItem.setPracticeMessage("§bYou are practicing. Do /unpractice to stop practicing.");
        pkuPracticeItem.setUnpracticeMessage("§aYou stopped practicing. Good luck!");
        pkuPracticeItem.setAlreadyPracticeMessage("§cYou are already practicing! Do /unpractice to stop practicing.");
        pkuPracticeItem.setNotInPracticeMessage("§cYou are not practicing! Do /practice to start practicing.");
        pkuPracticeItem.setFullInventoryMessage("§cYou cannot practice with a full inventory!");
        pkuPracticeItem.build();
        practiceItems.put("default", defaultPracticeItem);
        practiceItems.put("hpk", hpkPracticeItem);
        practiceItems.put("pku", pkuPracticeItem);
    }
}