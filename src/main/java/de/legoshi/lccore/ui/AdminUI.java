package de.legoshi.lccore.ui;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// PARTLY REFACTORED
public class AdminUI {

    public static Inventory inv;
    public static String inventory_name;
    public static final int inv_rows = 54;
    private static Linkcraft plugin;
    public static final Map<String, Integer> currentPage = new HashMap<>();

    public static void initialize() {
        plugin = Linkcraft.getInstance();
        inventory_name = Utils.chat("&8&lAdmin GUI&7");
        inv = Bukkit.createInventory(null, inv_rows);
    }

    public static Inventory GUI(Player p, int page) {
        if(inv == null) {
            initialize();
        }

        OfflinePlayer target = plugin.playerMap.get(p.getName());
        inv.clear();
        currentPage.put(p.getUniqueId().toString(), Integer.valueOf(page));
        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name + Utils.chat(" (page " + page + ")"));
        ConfigAccessor playerData = new ConfigAccessor(plugin, plugin.getPlayerdataFolder(), target.getUniqueId().toString() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        if (playerDataConfig.isConfigurationSection("Saves")) {
            Set<String> keys = playerDataConfig.getConfigurationSection("Saves").getKeys(false);
            int currentSlot = 0;
            for (String s : keys) {
                int saveIx = getIndex(keys, s);
                if (saveIx >= (page - 1) * 36 && saveIx < page * 36) {
                    String[] block = playerDataConfig.getString("Saves." + s + ".block").split(":");
                    int materialID = Integer.parseInt(block[0]);
                    int byteID = Integer.parseInt(block[1]);
                    String saveName = playerDataConfig.getString("Saves." + s + ".name");
                    String date = Utils.chat("&7(" + playerDataConfig.getString("Saves." + s + ".date") + ")");
                    String location = Utils.chat("&7Location:");
                    String location2 = Utils.chat(playerDataConfig.getString("Saves." + s + ".location"));
                    Utils.createItem(inv, materialID, byteID, 1, currentSlot, saveName, date, "", location, "&a" + location2.replace(":", "&2:&a"), "&cShift click to delete the save");
                    currentSlot++;
                }
            }
        }
        Utils.createItem(inv, 160, 14, 1, 36, " ", "");
        Utils.createItem(inv, 160, 15, 1, 37, " ", "");
        Utils.createItem(inv, 160, 15, 1, 38, " ", "");
        Utils.createItem(inv, 160, 15, 1, 39, " ", "");
        Utils.createItem(inv, 160, 3, 1, 40, " ", "");
        Utils.createItem(inv, 160, 15, 1, 41, " ", "");
        Utils.createItem(inv, 160, 15, 1, 42, " ", "");
        Utils.createItem(inv, 160, 1, 1, 43, " ", "");
        Utils.createItem(inv, 160, 1, 1, 44, " ", "");
        Utils.createItem(inv, 330, 0, 1, 45, "&cExit this menu", "");
        Utils.createItem(inv, 264, 0, 1, 49, "&bCommands:", "&f&o/checksaves &7&o<player> &f&o-> Check a player's saves");
        Utils.createItem(inv, 339, 0, 1, 52, "&6<-----", "&ePrevious Page");
        Utils.createItem(inv, 339, 0, 1, 53, "&6----->", "&eNext Page");
        toReturn.setContents(inv.getContents());
        return toReturn;
    }

    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        OfflinePlayer target = plugin.playerMap.get(p.getName());
        ConfigAccessor playerData = new ConfigAccessor(plugin, plugin.getPlayerdataFolder(), target.getUniqueId().toString() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        Set<String> keys = playerDataConfig.getConfigurationSection("Saves").getKeys(false);
        if (clicked.getType() == Material.AIR || clicked.getType() == null) return;
        if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&cExit this menu"))) {
            p.closeInventory();
        } else if (slot < 36) {
            if (!currentPage.containsKey(p.getUniqueId().toString())) {
                p.sendMessage(Utils.chat("&cSomething went wrong with your save, please try again."));
                p.closeInventory();
                return;
            }
            int page = currentPage.get(p.getUniqueId().toString());
            String locationString = playerDataConfig.getString("Saves." + keys.toArray()[slot + (page - 1) * 36] + ".location");
            Location loc = Utils.getLocationFromString(locationString);
            loc.setY(loc.getY());
            p.closeInventory();
            p.teleport(loc);
            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.43F);
        } else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&6<-----"))) {
            int page = currentPage.get(p.getUniqueId().toString());
            if (page > 1) {
                inv.clear();
                p.openInventory(GUI(p, page - 1));
            }
        } else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&6----->"))) {
            int page = currentPage.get(p.getUniqueId().toString());
            inv.clear();
            p.openInventory(GUI(p, page + 1));
        }
    }

    public static int getIndex(Set<?> set, Object value) {
        int result = 0;
        for (Object entry : set) {
            if (entry.equals(value)) return result;
            result++;
        }
        return -1;
    }
}