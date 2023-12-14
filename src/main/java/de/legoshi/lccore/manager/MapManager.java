package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.LCMap;
import de.legoshi.lccore.menu.MapStarComparator;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MapManager {

    Linkcraft plugin;
    InventoryManager im;
    static List<LCMap> maps;
    static ClickableItem[] mapItems;
    static FileConfiguration mapsConfig;

    public MapManager(Linkcraft plugin) {
        this.plugin = plugin;
        im = new InventoryManager(plugin);
        im.init();
        this.mapsConfig = plugin.mapsConfig.getConfig();
        loadMaps();
    }

    public static void loadMaps() {
        Linkcraft.getInstance().mapsConfig.reloadConfig();
        mapsConfig = Linkcraft.getInstance().mapsConfig.getConfig();
        maps = new ArrayList<>();

        Set<String> keys = mapsConfig.getConfigurationSection("maps").getKeys(false);

        for (String key : keys) {
            LCMap map = new LCMap();
            map.id = key;
            map.name = mapsConfig.getString(String.format("maps.%s.name", key));
            map.creator = mapsConfig.getString(String.format("maps.%s.creator", key));
            map.star_rating = mapsConfig.getDouble(String.format("maps.%s.star_rating", key));
            map.pp = mapsConfig.getDouble(String.format("maps.%s.pp", key));
            map.length = mapsConfig.getString(String.format("maps.%s.length", key));
            map.item = mapsConfig.getIntegerList(String.format("maps.%s.item", key));

            maps.add(map);
        }

        maps.sort(new MapStarComparator());

        loadMapItems();
    }

    private static void loadMapItems() {
        int amt = maps.size();
        ClickableItem[] ciArr = new ClickableItem[amt];

        int i = 0;
        for (LCMap map : maps) {
            ItemStack is = new ItemStack(Material.getMaterial(map.item.get(0)), 1, map.item.get(1).shortValue());
            ItemMeta im = is.getItemMeta();

            ArrayList<String> lore = new ArrayList<>();
            lore.add(Utils.chat("&7/warp " + map.id));
            lore.add(Utils.chat(String.format("&e&lStars: &e%.1f", map.star_rating)));
            lore.add(Utils.chat(String.format("&d&lPP: &d%.2f", map.pp)));
            lore.add(Utils.chat("&c&lLength: &c" + map.length));
            lore.add(Utils.chat("&a&aCreator: &a" + map.creator));
            im.setLore(lore);
            im.setDisplayName(Utils.chat("&l" + map.name));

            is.setItemMeta(im);

            ciArr[i] = ClickableItem.of(
                    is,
                    e -> Bukkit.dispatchCommand(e.getWhoClicked(), "warp " + map.id)
            );

            i++;
        }

        mapItems = ciArr;
    }

    public static ClickableItem[] getMapItems() {
        return mapItems;
    }

}
