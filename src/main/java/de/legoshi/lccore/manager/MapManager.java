package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.menu.maps.LCMap;
import de.legoshi.lccore.util.MapType;
import de.legoshi.lccore.util.SheetsAPI;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

public class MapManager {
    private final static String sheetId = "1IRRRxXs0998rA2H-CF1vD6oFRAe-n-ewSF0dLOzTups";
    private final static String range = "Sheet1!A2:I1000";
    private final static String fileName = "maps.yml";
    @Getter private List<LCMap> maps = new ArrayList<>();
    @Getter private HashMap<String, LCMap> mapMap = new HashMap<>();
    @Inject private PlayerManager playerManager;
    @Inject private DBManager db;

    public void updateMaps() throws GeneralSecurityException, IOException {
        List<LCMap> maps = parseSheetsDataToMapsList(SheetsAPI.getData(sheetId, range));
        createYMLFromMaps(maps);
        cacheMaps();
    }

    public static List<LCMap> parseSheetsDataToMapsList(List<List<Object>> values) {
        List<LCMap> maps = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            for (List row : values) {
                LCMap lcMap = new LCMap();
                Object id = row.get(0);
                if(id == null || ((String)id).isEmpty()) {
                    continue;
                }

                lcMap.setId((String)id);
                lcMap.setName((String)row.get(1));
                lcMap.setCreator((String) row.get(2));
                lcMap.setStar(Double.parseDouble((String) row.get(3)));
                lcMap.setPp(Integer.parseInt((String)row.get(4)));
                lcMap.setLength((String)row.get(5));
                lcMap.setItem(new ItemStack(Integer.parseInt((String)row.get(6)), 1, Short.parseShort((String)row.get(7))));
                lcMap.setMapType(MapType.valueOf((String)row.get(8)));
                maps.add(lcMap);
            }
        }

        return maps;
    }

    public static void createYMLFromMaps(List<LCMap> maps) {
        if(maps.isEmpty()) {
            return;
        }

        Map<String, Map<String, Object>> yamlData = new HashMap<>();
        for (LCMap map : maps) {
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("name", map.getName());
            mapData.put("creator", map.getCreator());
            mapData.put("star_rating", map.getStar());
            mapData.put("pp", (int)map.getPp());
            mapData.put("length", map.getLength());
            mapData.put("item", new int[]{map.getItem().getTypeId(), map.getItem().getDurability()});
            mapData.put("type", map.getMapType().name());

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

        for (String key : keys) {
            LCMap map = new LCMap();
            map.setId(key);
            map.setName(mapsConfig.getString(String.format("maps.%s.name", key)));
            map.setCreator(mapsConfig.getString(String.format("maps.%s.creator", key)));
            map.setStar(mapsConfig.getDouble(String.format("maps.%s.star_rating", key)));
            map.setPp(mapsConfig.getDouble(String.format("maps.%s.pp", key)));
            map.setLength(mapsConfig.getString(String.format("maps.%s.length", key)));
            List<Integer> itemValues = mapsConfig.getIntegerList(String.format("maps.%s.item", key));
            map.setItem(new ItemStack(Material.getMaterial(itemValues.get(0)), 1, itemValues.get(1).shortValue()));
            map.setMapType(MapType.valueOf(mapsConfig.getString(String.format("maps.%s.type", key))));

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
}
