package de.legoshi.lccore.ui;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.MessageUtil;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

// PARTLY REFACTORED
public class SaveCreationUI {

    public static Inventory inv;
    public static String inventory_name;
    public final static int inv_rows = 27;
    private static Linkcraft plugin;

    public static void initialize() {
        plugin = Linkcraft.getPlugin();
        inventory_name = Utils.chat("&8&lSave Creation");
        inv = Bukkit.createInventory(null, inv_rows);
    }

    public static Inventory GUI(Player p) {
        if(inv == null) {
            initialize();
        }

        Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
        Utils.createItem(inv, 160, 0, 1, 1, " ", "");
        Utils.createItem(inv, 160, 0, 1, 2, " ", "");
        Utils.createItem(inv, 160, 0, 1, 3, " ", "");
        Utils.createItem(inv, 160, 0, 1, 10, " ", "");
        Utils.createItem(inv, 160, 0, 1, 12, " ", "");
        Utils.createItem(inv, 160, 0, 1, 19, " ", "");
        Utils.createItem(inv, 160, 0, 1, 20, " ", "");
        Utils.createItem(inv, 160, 0, 1, 21, " ", "");
        Utils.createItem(inv, 160, 5, 1, 5, " ", "");
        Utils.createItem(inv, 160, 5, 1, 6, " ", "");
        Utils.createItem(inv, 160, 5, 1, 7, " ", "");
        Utils.createItem(inv, 160, 5, 1, 14, " ", "");
        Utils.createItem(inv, 160, 5, 1, 16, " ", "");
        Utils.createItem(inv, 160, 5, 1, 23, " ", "");
        Utils.createItem(inv, 160, 5, 1, 24, " ", "");
        Utils.createItem(inv, 160, 5, 1, 25, " ", "");
        ItemStack randomItem = Utils.getRandomVisibleBlock();
        Utils.createItem(inv, randomItem.getTypeId(), randomItem.getDurability(), 1, 11, "", "&oClick this block to reroll it!");
        Utils.createItem(inv, 388, 0, 1, 15, "&a&lCreate save", "&e&oDo /saves to access this save once you create it!");
        toReturn.setContents(inv.getContents());
        return toReturn;
    }

    public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
        String uuid = p.getUniqueId().toString();
        if (clicked.getItemMeta().getLore().toArray()[0].equals(Utils.chat("&oClick this block to reroll it!"))) {
            ItemStack randomItem = Utils.getRandomVisibleBlock();
            Utils.createItem(inv, randomItem.getTypeId(), randomItem.getDurability(), 1, 11, "", "&oClick this block to reroll it!");
        } else if (clicked.getItemMeta().getDisplayName().equals(Utils.chat("&a&lCreate save"))) {

            if (!p.hasPermission("ps.save") || (p.hasPermission("lc.normal") && p.getWorld().getName().equals("bonus"))) {
                p.sendMessage(Utils.chat("&cYou don't have permission to do this command! (ps.save)"));
                return;
            }

            int saveNumber;
            ConfigAccessor playerData = new ConfigAccessor(plugin, plugin.getPlayerdataFolder(), uuid + ".yml");
            FileConfiguration playerDataConfig = playerData.getConfig();
            String saveName = plugin.saveCreations.get(uuid);
            if (playerDataConfig.isConfigurationSection("Saves")) {
                Set<String> saves = playerDataConfig.getConfigurationSection("Saves").getKeys(false);
                if (!saves.isEmpty()) {
                    saveNumber = Integer.parseInt((String) saves.toArray()[saves.size() - 1]) + 1;
                } else {
                    saveNumber = 1;
                }
            } else {
                saveNumber = 1;
            }
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            playerDataConfig.set("Saves." + saveNumber + ".name", saveName);
            playerDataConfig.set("Saves." + saveNumber + ".block", inv.getItem(11).getTypeId() + ":" + inv.getItem(11).getDurability());
            playerDataConfig.set("Saves." + saveNumber + ".location", Utils.getStringFromLocation(p.getLocation()));
            playerDataConfig.set("Saves." + saveNumber + ".date", format.format(now));
            playerData.saveConfig();
            MessageUtil.log(p.getName() + " created a save at: " + Utils.getStringFromLocation(p.getLocation()), true);
            FileConfiguration config = plugin.getConfig();
            List<String> commandsToRun = config.getStringList("save-commands");

            if (!commandsToRun.isEmpty()) {
                Map<String, String> values = new HashMap<>();
                values.put("player", p.getName());
                StrSubstitutor sub = new StrSubstitutor(values, "[", "]");
                for (int i = 0; i < commandsToRun.size(); i++) {
                    String commandToRun = sub.replace(commandsToRun.toArray()[i]);
                    Linkcraft.consoleCommand(commandToRun);
                }
            }
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.43F);
            p.sendMessage(Utils.chat("&aSuccessfully created a new save!"));
        }
    }
}