package de.legoshi.lccore.util;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.menu.LCMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapUpdater {
    private final static String sheetId = "1IRRRxXs0998rA2H-CF1vD6oFRAe-n-ewSF0dLOzTups";
    private final static String range = "Sheet1!A2:H1000";
    private final static String fileName = "maps.yml";

    public static void update() throws GeneralSecurityException, IOException {
        List<LCMap> maps = getParsedMaps(SheetsAPI.getData(sheetId, range));
        createYMLFromData(maps);
        MapManager.loadMaps();
    }

    public static void createYMLFromData(List<LCMap> maps) {
        if(maps.isEmpty()) {
            return;
        }

        Map<String, Map<String, Object>> yamlData = new HashMap<>();
        for (LCMap map : maps) {
            Map<String, Object> mapData = new HashMap<>();
            mapData.put("name", map.name);
            mapData.put("creator", map.creator);
            mapData.put("star_rating", map.star_rating);
            mapData.put("pp", (int)map.pp);
            mapData.put("length", map.length);
            mapData.put("item", map.item.toArray());

            yamlData.put(map.id, mapData);
        }

        Map<String, Object> yamlResult = new HashMap<>();
        yamlResult.put("maps", yamlData);

        try (FileWriter writer = new FileWriter(Linkcraft.getInstance().getDataFolder().getPath() + File.separator + fileName)) {
            new Yaml().dump(yamlResult, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LCMap> getParsedMaps(List<List<Object>> values) {
        List<LCMap> maps = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            for (List row : values) {
                LCMap lcMap = new LCMap();

                lcMap.id = (String)row.get(0);
                lcMap.name = (String) row.get(1);
                lcMap.creator = (String) row.get(2);
                lcMap.star_rating = Double.parseDouble((String) row.get(3));
                lcMap.pp = Integer.parseInt((String) row.get(4));
                lcMap.length = (String) row.get(5);

                List<Integer> item = new ArrayList<>();
                item.add(Integer.parseInt((String) row.get(6)));
                item.add(Integer.parseInt((String) row.get(7)));
                lcMap.item = item;
                maps.add(lcMap);
            }
        }

        return maps;
    }



}