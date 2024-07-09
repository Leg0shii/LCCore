package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.menu.maps.LCMap;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.function.Consumer;

public class MapManager {
    private final static String sheetId = "1IRRRxXs0998rA2H-CF1vD6oFRAe-n-ewSF0dLOzTups";
    private final static String range = "Sheet1!A2:S1000";
    private final static String fileName = "maps.yml";

    @Getter private List<LCMap> maps = new ArrayList<>();
    @Getter private HashMap<String, LCMap> mapMap = new HashMap<>();
    @Inject private PlayerManager playerManager;
    @Inject private LuckPermsManager lpManager;
    @Inject private DBManager db;
    @Inject private ConfigManager configManager;

    public void updateMaps() throws GeneralSecurityException, IOException {
        List<LCMap> maps = parseSheetsDataToMapsList(SheetsAPI.getData(sheetId, range));
        createYMLFromMaps(maps);
        cacheMaps();
    }

    public static List<LCMap> parseSheetsDataToMapsList(List<List<Object>> values) {
        List<LCMap> maps = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            for (List<Object> row : values) {
                LCMap lcMap = parseRowToLCMap(row);
                if (lcMap != null) {
                    maps.add(lcMap);
                }
            }
        }
        return maps;
    }

    private static LCMap parseRowToLCMap(List<Object> row) {
        LCMap lcMap = new LCMap();
        Object id = row.get(0);
        if (id == null || ((String)id).isEmpty()) {
            return null;
        }

        lcMap.setId((String)id);
        lcMap.setName(parseString(row.get(1)));
        lcMap.setCreator(parseString(row.get(2)));
        lcMap.setStar(parseDouble(row.get(3)));
        String pp = parseString(row.get(4));
        if(pp != null) {
            String[] ppData = pp.split(":");
            if (ppData.length > 1) {
                if (ppData[0].equalsIgnoreCase("display")) {
                    lcMap.setPpReward(false);
                }
                lcMap.setPp(parseDouble(ppData[1]));
            } else {
                lcMap.setPp(parseDouble(pp));
                lcMap.setPpReward(null);
            }
        } else {
            lcMap.setPp(null);
        }

        lcMap.setLength(parseString(row.get(5)));
        lcMap.setItem(parseItem(row.get(6), row.get(7)));
        lcMap.setMapType(parseMapType(row.get(8)));
        lcMap.setVictoryWarp(parseString(row.get(9)));
        lcMap.setRank(parseString(row.get(10)));
        parseList(row.get(11), lcMap::setTags);
        parseSound(row.get(12), lcMap);
        parseItemRewards(row.get(13), lcMap);
        parseList(row.get(14), lcMap::setBroadcasts);
        parseList(row.get(15), lcMap::setPlayerMessages);
        lcMap.setForceBan(parseBanType(row.get(16)));
        lcMap.setDisplay(parseString(row.get(17)));
        parseBoolean(row.get(18), lcMap::setNoPrac);
        return lcMap;
    }

    private static void parseSound(Object value, LCMap lcMap) {
        if (value != null && !"N/A".equals(value)) {
            String[] soundData = ((String)value).split("\\|");
            if (soundData.length == 3) {
                lcMap.setSound(Sound.valueOf(soundData[0]));
                lcMap.setSoundVolume(Float.parseFloat(soundData[1]));
                lcMap.setSoundPitch(Float.parseFloat(soundData[2]));
            }
        }
    }

    private static void parseItemRewards(Object value, LCMap lcMap) {
        if (value != null && !"N/A".equals(value)) {
            String[] itemData = ((String)value).split("\\|");
            List<ItemStack> items = new ArrayList<>();
            for (String item : itemData) {
                String[] itemValues = item.split(":");
                items.add(new ItemStack(Integer.parseInt(itemValues[0]), 1, Short.parseShort(itemValues[1])));
            }
            lcMap.setItemRewards(items);
        }
    }

    private static void parseList(Object value, Consumer<List<String>> setter) {
        if (value != null && !"N/A".equals(value)) {
            String[] items = ((String)value).split("\\|");
            setter.accept(Arrays.asList(items));
        }
    }

    private static MapType parseMapType(Object value) {
        return (value != null && !"N/A".equals(value)) ? MapType.valueOf((String) value) : null;
    }

    private static BanType parseBanType(Object value) {
        return (value != null && !"N/A".equals(value)) ? BanType.valueOf((String) value) : null;
    }

    private static String parseString(Object value) {
        return (value != null && !"N/A".equals(value)) ? (String) value : null;
    }

    private static Double parseDouble(Object value) {
        return (value != null && !"N/A".equals(value)) ? Double.parseDouble((String) value) : null;
    }

    private static Integer parseInteger(Object value) {
        return (value != null && !"N/A".equals(value)) ? Integer.parseInt((String) value) : null;
    }

    private static void parseBoolean(Object value, Consumer<Boolean> setter) {
        if(value != null && !"N/A".equals(value)) {
            setter.accept(Boolean.parseBoolean((String)value));
        }
    }

    private static ItemStack parseItem(Object id, Object damage) {
        if (id != null && !"N/A".equals(id)) {
            return new ItemStack(Integer.parseInt((String) id), 1, Short.parseShort((String) damage));
        }
        return null;
    }

    private static void store(Map<String, Object> mapData, String key, Object value) {
        if(value != null) {
            mapData.put(key, value);
        }
    }


    public static void createYMLFromMaps(List<LCMap> maps) {
        if(maps.isEmpty()) {
            return;
        }

        Map<String, Map<String, Object>> yamlData = new HashMap<>();
        for (LCMap map : maps) {
            Map<String, Object> mapData = new HashMap<>();
            store(mapData, "name", map.getName());
            store(mapData, "creator", map.getCreator());
            store(mapData, "star_rating", map.getStar());
            store(mapData, "pp", map.getPp());
            store(mapData, "pp_reward", map.getPpReward());
            store(mapData, "length", map.getLength());
            if(map.getItem() != null) {
                store(mapData, "item", new int[]{map.getItem().getTypeId(), map.getItem().getDurability()});
            }
            if(map.getMapType() == null) {
                MessageUtil.log(Message.LOADING_MAPS_FAILED, true, map.getId(), "map type");
                continue;
            }
            store(mapData, "type", map.getMapType().name());
            store(mapData, "victory_warp", map.getVictoryWarp());
            store(mapData, "rank", map.getRank());
            store(mapData, "tags", map.getTags());

            if(map.getSound() != null) {
                Float volume = map.getSoundVolume() == null ? 1 : map.getSoundVolume();
                Float pitch = map.getSoundPitch() == null ? 1 : map.getSoundPitch();
                store(mapData, "audio", new String[]{map.getSound().name(), String.valueOf(volume), String.valueOf(pitch)});
            }

            if(map.getItemRewards() != null) {
                List<int[]> items = new ArrayList<>();
                for(ItemStack item : map.getItemRewards()) {
                    items.add(new int[]{item.getTypeId(), item.getDurability()});
                }
                store(mapData, "item_reward", items);
            }

            store(mapData, "broadcast", map.getBroadcasts());
            store(mapData, "player_message", map.getPlayerMessages());

            if(map.getForceBan() != null) {
                store(mapData, "force_ban", map.getForceBan().name());
            }

            store(mapData, "display", map.getDisplay());
            store(mapData, "no_prac", map.getNoPrac());

            yamlData.put(map.getId(), mapData);
        }

        Map<String, Object> yamlResult = new HashMap<>();
        yamlResult.put("maps", yamlData);

        try (FileWriter writer = new FileWriter(Linkcraft.getPlugin().getDataFolder().getPath() + File.separator + fileName)) {
            new Yaml().dump(yamlResult, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadMapConfig() {
        Linkcraft.getPlugin().mapsConfig.reloadConfig();
    }

    private FileConfiguration getMapConfig() {
        reloadMapConfig();
        return Linkcraft.getPlugin().mapsConfig.getConfig();
    }

    public void cacheMaps() {
        maps.clear();
        mapMap.clear();
        FileConfiguration mapsConfig = getMapConfig();
        Set<String> keys = mapsConfig.getConfigurationSection("maps").getKeys(false);
        for(String key : keys) {
            LCMap map = new LCMap();
            map.setId(key);
            map.setName(mapsConfig.getString(String.format("maps.%s.name", key)));
            map.setCreator(mapsConfig.getString(String.format("maps.%s.creator", key)));
            map.setStar(mapsConfig.getDouble(String.format("maps.%s.star_rating", key)));
            map.setPp(mapsConfig.getDouble(String.format("maps.%s.pp", key)));
            try {
                String temp = mapsConfig.getString(String.format("maps.%s.pp_reward", key));
                if(temp == null) {
                    map.setPpReward(null);
                } else {
                    map.setPpReward(Boolean.parseBoolean(mapsConfig.getString(String.format("maps.%s.pp_reward", key))));
                }
            } catch (Exception e) {
                map.setPpReward(null);
            }
            map.setLength(mapsConfig.getString(String.format("maps.%s.length", key)));
            List<Integer> itemValues = mapsConfig.getIntegerList(String.format("maps.%s.item", key));
            if(itemValues != null && !itemValues.isEmpty()) {
                map.setItem(new ItemStack(Material.getMaterial(itemValues.get(0)), 1, itemValues.get(1).shortValue()));
            } else {
                map.setItem(null);
            }

            String mapType = mapsConfig.getString(String.format("maps.%s.type", key));
            if(mapType == null) {
                MessageUtil.log(Message.LOADING_MAPS_FAILED, true, key, "map type");
                continue;
            }
            map.setMapType(MapType.valueOf(mapType));
            map.setVictoryWarp(mapsConfig.getString(String.format("maps.%s.victory_warp", key)));
            map.setRank(mapsConfig.getString(String.format("maps.%s.rank", key)));
            map.setTags(mapsConfig.getStringList(String.format("maps.%s.tags", key)));
            List<String> audioInfo = mapsConfig.getStringList(String.format("maps.%s.audio", key));

            if(audioInfo != null && audioInfo.size() == 3) {
                map.setSound(Sound.valueOf(audioInfo.get(0)));
                map.setSoundVolume(Float.parseFloat(audioInfo.get(1)));
                map.setSoundPitch(Float.parseFloat(audioInfo.get(2)));
            } else {
                map.setSound(null);
                map.setSoundVolume(null);
                map.setSoundPitch(null);
            }

            List<List<Integer>> itemRewards = (List<List<Integer>>) mapsConfig.getList(String.format("maps.%s.item_reward", key));
            List<ItemStack> rewards = new ArrayList<>();
            if (itemRewards != null) {
                for (List<Integer> reward : itemRewards) {
                    rewards.add(new ItemStack(Material.getMaterial(reward.get(0)), 1, reward.get(1).shortValue()));
                }
            }
            map.setItemRewards(rewards);
            map.setBroadcasts(mapsConfig.getStringList(String.format("maps.%s.broadcast", key)));
            map.setPlayerMessages(mapsConfig.getStringList(String.format("maps.%s.player_message", key)));

            String forceBan = mapsConfig.getString(String.format("maps.%s.force_ban", key));
            if(forceBan != null) {
                map.setForceBan(BanType.valueOf(forceBan));
            }

            map.setDisplay(mapsConfig.getString(String.format("maps.%s.display", key)));
            map.setNoPrac(Boolean.parseBoolean(mapsConfig.getString(String.format("maps.%s.no_prac", key))));

            maps.add(map);
            mapMap.put(key, map);
        }
    }

    public boolean mapExists(String name) {
        return mapMap.containsKey(name);
    }

    public List<String> getMapNames() {
        return new ArrayList<>(mapMap.keySet());
    }

    public Map<String, PlayerCompletion> getPlayerMapData(String uuid) {
        String hql = "SELECT c FROM PlayerCompletion c " +
                "WHERE c.player = :player";

        EntityManager em = db.getEntityManager();
        TypedQuery<PlayerCompletion> query = em.createQuery(hql, PlayerCompletion.class);

        query.setParameter("player", new LCPlayerDB(uuid));
        Map<String, PlayerCompletion> playerMapData = new HashMap<>();

        for(PlayerCompletion completion : query.getResultList()) {
            playerMapData.put(completion.getMap(), completion);
        }

        em.close();
        return playerMapData;
    }

    public boolean hasPreviousCompletion(String uuid, String map) {
        return getPreviousCompletion(uuid, map) != null;
    }

    public PlayerCompletion getPreviousCompletion(String uuid, String map) {
        return db.find(new PlayerCompletionId(uuid, map), PlayerCompletion.class);
    }

    public LCMap getMap(String name) {
        return mapMap.get(name);
    }

    public void giveCompletionNoRewards(CommandSender sender, PlayerRecord record, LCMap map, boolean dates) {
        String uuid = record.getUuid();
        PlayerCompletion playerCompletion = getPreviousCompletion(uuid, map.getId());
        boolean firstCompletion = playerCompletion == null;
        LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(uuid);

        if(firstCompletion) {
            playerCompletion = new PlayerCompletion();
            playerCompletion.setPlayer(lcPlayerDB);
            playerCompletion.setMap(map.getId());
            if(dates) {
                playerCompletion.setFirst(new Date());
                playerCompletion.setLatest(new Date());
            }
            playerCompletion.setCompletions(1);
            db.persist(playerCompletion, lcPlayerDB);
            MessageUtil.send(Message.MAP_FIRST_COMPLETION, sender, map.getId(), record.getName());
        } else {
            playerCompletion.setCompletions(playerCompletion.getCompletions() + 1);
            if(dates) {
                playerCompletion.setLatest(new Date());
            }
            db.update(playerCompletion);
            MessageUtil.send(Message.MAP_NEW_COMPLETION, sender, map.getId(), record.getName());
        }
    }

    public boolean requiresBanToComplete(Player player, LCMap lcMap, PlayerCompletion prev) {
        if(prev != null && prev.isReceivedBan()) {
            return false;
        }

        if((lcMap.getStar() == null || lcMap.getStar() < 6) && lcMap.getForceBan() == null) {
            return false;
        }

        if(lcMap.getForceBan() != null) {
            if(lcMap.getForceBan().equals(BanType.FIRST)) {
                if(prev != null && prev.getCompletions() > 0) {
                    MessageUtil.send(Message.MAP_BYPASS_BAN, player);
                    return false;
                }
                return true;
            } else if(lcMap.getForceBan().equals(BanType.ALWAYS)) {
                return true;
            }
        }

        if(lcMap.getStar() >= 6) {
            if(prev != null && prev.getCompletions() > 0) {
                MessageUtil.send(Message.MAP_BYPASS_BAN, player);
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    public void giveRewards(Player player, LCMap map) {
        giveRankReward(player, map);
        giveTagReward(player, map);
        giveAudioReward(map);
        giveItemReward(player, map);
        givePPReward(player, map);
        giveBroadcastReward(player, map);
        givePlayerMessageReward(player, map);
        givePermissionReward(player, map);
    }

    private void givePermissionReward(Player player, LCMap map) {
        if(map.getStar() == null || map.getStar() == 0) {
            return;
        }
        if(map.getStar() >= 6) {
            lpManager.givePermission(player, "essentials.kittycannon");
            lpManager.givePermission(player, "essentials.chat.format");
        }
    }

    private void givePPReward(Player player, LCMap map) {
        if(map.getPpReward() != null && !map.getPpReward()) {
            return;
        }

        if(map.getPp() == null || map.getPp() == 0) {
            return;
        }
        Linkcraft.consoleCommand("eco give " + player.getName() + " " + map.getPp());
    }

    private void givePlayerMessageReward(Player player, LCMap map) {
        if(map.getPlayerMessages() == null || map.getPlayerMessages().isEmpty()) {
            return;
        }

        for(String message : map.getPlayerMessages()) {
            player.sendMessage(GUIUtil.colorize(message));
        }
    }

    private void giveBroadcastReward(Player player, LCMap map) {
        if(map.getBroadcasts() != null && !map.getBroadcasts().isEmpty() && map.getBroadcasts().get(0).equalsIgnoreCase("NONE")) {
            return;
        }

        switch (map.getMapType()) {
            case RANKUP:
            case WOLF:
            case MAZE:
            case BONUS:
            case CHALLENGE:
                if(map.getBroadcasts() == null || map.getBroadcasts().isEmpty()) {
                    return;
                }

                List<String> broadcasts = map.getBroadcasts();
                for(int i = 0; i < broadcasts.size(); i++) {
                    String result;
                    String broadcast = broadcasts.get(i);
                    if(i == 0) {
                        String formatted = formatBroadcast(broadcast, player, map);
                        result = GUIUtil.colorize(map.getMapType().prefix + ChatColor.RESET + " " + formatted);
                    } else {
                        result =  GUIUtil.colorize(formatBroadcast(broadcast, player, map));
                    }

                    MessageUtil.broadcast(result);
                    MessageUtil.log(result, false);
                }
                break;
            case SIDE:
                if(map.getBroadcasts() == null || map.getBroadcasts().isEmpty()) {
                    sideMapDefaultBroadcast(player, map);
                }

                for(String bcast : map.getBroadcasts()) {
                    String result = GUIUtil.colorize(formatBroadcast(bcast, player, map));
                    MessageUtil.broadcast(result);
                    MessageUtil.log(result, false);
                }
                break;
            case MISC:
                if(map.getBroadcasts() == null) {
                    return;
                }

                for(String bcast : map.getBroadcasts()) {
                    String result = GUIUtil.colorize(formatBroadcast(bcast, player, map));
                    MessageUtil.broadcast(result);
                    MessageUtil.log(result, false);
                }
                break;
        }
    }


    private void sideMapDefaultBroadcast(Player player, LCMap map) {
        String mapDisplay = map.getDisplay() != null ? map.getDisplay() : map.getName();
        String broadcastMessage = GUIUtil.colorize(map.getMapType().prefix + ChatColor.RESET + " &4&l" + player.getName() + "&7 has finished &r" + mapDisplay + "! " + getStarDisplay(map.getStar()));
        String selfMessage = GUIUtil.colorize(map.getMapType().prefix + ChatColor.RESET + " &7You have just completed &r" + mapDisplay + "! " + getStarDisplay(map.getStar()));
        if(map.getStar() < 4) {
            player.sendMessage(selfMessage);
        } else {
            MessageUtil.broadcast(broadcastMessage);
        }

        MessageUtil.log(broadcastMessage, false);
    }

    private String getStarDisplay(Double star) {
        if(star == null) {
            return "N/A";
        }

        String starStr = getMapStar(star);

        if(star >= 7) {
            return "&e&l" + starStr + "*";
        } else {
            return "&e" + starStr + "*";
        }

    }

    private String getMapStar(Double star) {
        if(star == null) {
            return "N/A";
        }

        if(star % 1 == 0) {
            return String.valueOf(star.intValue());
        } else {
            return String.valueOf(star);
        }
    }

    private String formatBroadcast(String broadcast, Player player, LCMap map) {
        return broadcast.replace("{player}", player.getName()).replace("{rank}", retrieveRankByMapType(map));
    }

    private String retrieveRankByMapType(LCMap map) {
        switch (map.getMapType()) {
            case RANKUP:
                return ConfigManager.ranksDisplay.get(map.getRank()).getDisplay();
            case WOLF:
                return ConfigManager.wolfDisplay.get(map.getRank()).getDisplay();
            case BONUS:
                return ConfigManager.bonusDisplay.get(map.getRank()).getDisplay();
            default:
                return map.getRank() != null ? map.getRank() : "ERROR";
        }
    }

    private void giveItemReward(Player player, LCMap map) {
        if(map.getItemRewards() == null || map.getItemRewards().isEmpty()) {
            return;
        }

        for(ItemStack rewardItem : map.getItemRewards()) {
            if(!ItemUtil.isFullInventory(player)) {
                player.getInventory().addItem(rewardItem);
                return;
            }

            MessageUtil.send(Message.MAP_DROPPING_ITEM, player);
            Linkcraft.syncLater(() -> {
                Player updatedLocPlayer = Bukkit.getPlayer(player.getName());
                if(updatedLocPlayer.isOnline()) {
                    player.getWorld().dropItem(player.getLocation(), rewardItem);
                }
            }, 20L * 3);
        }

    }

    private void giveAudioReward(LCMap map) {
        if(map.getSound() == null) {
            return;
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getEyeLocation(), map.getSound(), map.getSoundVolume(), map.getSoundPitch());
        }
    }

    private void giveTagReward(Player player, LCMap map) {
        if(map.getTags() == null || map.getTags().isEmpty()) {
            return;
        }

        for(String tag : map.getTags()) {
            Linkcraft.consoleCommand("tags unlock " + tag + " " + player.getName());
        }
    }

    private void giveRankReward(Player player, LCMap map) {
        if(map.getRank() == null) {
            return;
        }

        switch (map.getMapType()) {
            case RANKUP:
            case WOLF:
                lpManager.giveGroup(player, map.getRank());
                break;
            case MAZE:
            case BONUS:
            case CHALLENGE:
                lpManager.givePermission(player, map.getRank());
                break;
        }
    }

    public List<LCMap> findMapByTagReward(String tagId) {
        List<LCMap> foundMaps = new ArrayList<>();
        for(LCMap map : maps) {
            for(String tag : map.getTags()) {
                if(tag.equalsIgnoreCase(tagId)) {
                    foundMaps.add(map);
                }
            }
        }
        return foundMaps;
    }
}
