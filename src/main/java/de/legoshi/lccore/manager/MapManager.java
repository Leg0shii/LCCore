package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.LCMap;
import de.legoshi.lccore.menu.MapComparator;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.MapType;
import de.legoshi.lccore.util.Utils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MapManager {

    public final Linkcraft plugin;
    public final InventoryManager im;
    static List<LCMap> maps;
    public static HashMap<String, LCMap> mapMap;
    static ClickableItem[] mapItems;
    static FileConfiguration mapsConfig;

    public MapManager(Linkcraft plugin) {
        this.plugin = plugin;
        im = new InventoryManager(plugin);
        im.init();
        mapsConfig = plugin.mapsConfig.getConfig();
        loadMaps();
    }

    public static boolean mapExists(String mapName) {
        return mapMap.containsKey(mapName);
    }

    public static void loadMaps() {
        Linkcraft.getPlugin().mapsConfig.reloadConfig();
        mapsConfig = Linkcraft.getPlugin().mapsConfig.getConfig();
        maps = new ArrayList<>();
        mapMap = new HashMap<>();

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
            map.mapType = MapType.valueOf(mapsConfig.getString(String.format("maps.%s.type", key)));

            maps.add(map);
            mapMap.put(key, map);
        }

        maps.sort(new MapComparator());
    }

    public static ClickableItem[] getMapItems(String uuid, Player holder, boolean admin) {
        List<ClickableItem> clickableItems = new ArrayList<>();
        ConfigAccessor playerConfigAccessor = Utils.getPlayerConfig(uuid);
        FileConfiguration playerConfig = playerConfigAccessor.getConfig();

        for (LCMap map : maps) {
            if(!map.mapType.equals(MapType.SIDE)) {
                continue;
            }
            ConfigurationSection mapsSection = playerConfig.getConfigurationSection("maps");
            if(mapsSection == null) {
                playerConfig.createSection("maps");
                playerConfig.getConfigurationSection("maps");
                mapsSection = playerConfig.getConfigurationSection("maps");
            }

            ConfigurationSection mapSection = playerConfig.getConfigurationSection("maps." + map.id);

            boolean completedMap = mapSection != null;
            ItemStack is = new ItemStack(Material.getMaterial(map.item.get(0)), 1, map.item.get(1).shortValue());

            if (completedMap) {
                Utils.addGlow(is);
            }

            ItemMeta im = is.getItemMeta();

            ArrayList<String> lore = new ArrayList<>();
            lore.add(Utils.chat("&7/warp " + map.id));
            lore.add(Utils.chat(String.format("&e&lStars: &e%.1f", map.star_rating)));
            lore.add(Utils.chat(String.format("&d&lPP: &d" + (int)map.pp)));
            lore.add(Utils.chat("&c&lLength: &c" + map.length));
            lore.add(Utils.chat("&a&lCreator: &a" + map.creator));
            if(completedMap) {
                lore.add(Utils.chat("&b&lCompletions: &b" + mapSection.get("completions")));
                lore.add(Utils.chat("&6&lFirst: &6" + Utils.stringToISO(mapSection.getString("firstcompletion"))));
                lore.add(Utils.chat("&6&lLatest: &6" + Utils.stringToISO(mapSection.getString("lastcompletion"))));
            }

            if(admin) {
                lore.add(Utils.chat("&8Left Click - Add Completion"));
                lore.add(Utils.chat("&8Right Click - Remove All Completions"));
            }
            im.setLore(lore);
            im.setDisplayName(Utils.chat("&f&l" + map.name));

            is.setItemMeta(im);


            ConfigurationSection finalMapsSection = mapsSection;

            clickableItems.add(ClickableItem.of(
                    is,
                    e -> {
                        if(admin) {
                            if(e.getClick().isRightClick()) {
                                finalMapsSection.set(map.id, null);
                                holder.sendMessage(Utils.chat("&aRemoved completions of " + map.id));
                            } else if(e.getClick().isLeftClick()) {
                                ConfigurationSection lamMapConfig = playerConfig.getConfigurationSection("maps." + map.id);
                                if(completedMap) {
                                    int completions = lamMapConfig.getInt("completions");
                                    lamMapConfig.set("completions", completions + 1);
                                    lamMapConfig.set("lastcompletion", "N/A");
                                } else {
                                    finalMapsSection.createSection(map.id);
                                    lamMapConfig = finalMapsSection.getConfigurationSection(map.id);
                                    lamMapConfig.set("firstcompletion", "N/A");
                                    lamMapConfig.set("completions", 1);
                                    lamMapConfig.set("lastcompletion", "N/A");
                                }
                                holder.sendMessage(Utils.chat("&aAdded a completion of " + map.id));
                            }
                            playerConfigAccessor.saveConfig();
                            holder.closeInventory();
                        } else {
                            Bukkit.dispatchCommand(e.getWhoClicked(), "warp " + map.id);
                        }
                    }
            ));
        }

        return clickableItems.toArray(new ClickableItem[0]);
    }

}
