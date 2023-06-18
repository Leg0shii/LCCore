package de.legoshi.lccore.util;

import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

// PARTLY REFACTORED
public class Utils {

    public static String getStringFromLocation(Location l) {
        if (l == null)
            return "";
        return l.getWorld().getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ() + ":" + l.getYaw() + ":" + l.getPitch();
    }

    public static Location getLocationFromString(String s) {
        if (s == null || s.trim() == "")
            return null;
        String[] parts = s.split(":");
        if (parts.length == 6) {
            World w = Bukkit.getServer().getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }

    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> chat(String... message) {
        List<String> parts = new ArrayList<>();
        for (String line : message) {
            parts.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return parts;
    }

    public static ItemStack createItem(Inventory inv, int materialID, int byteID, int amount, int invSlot, String displayName, String... loreString) {
        List<String> lore = new ArrayList<>();
        ItemStack item = new ItemStack(Material.getMaterial(materialID), amount, (short) byteID);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chat(displayName));
        for (String s : loreString) {
            lore.add(chat(s));
        }
        if (lore.size() > 0) meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(invSlot, item);
        return item;
    }

    public static ItemStack getRandomVisibleBlock() {
        List<Material> blocks = new ArrayList<>();
        int[] disabledBlocks = {0, 8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 60, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 99, 100, 104, 105, 115, 117, 118, 119, 122, 124, 125, 127, 132, 140, 141, 142, 144, 149, 150, 176, 177, 178, 181};
        for (Material block : Material.values()) {
            if (block.isBlock() && IntStream.of(disabledBlocks).noneMatch(x -> (x == block.getId())) && (block.getId() < 193 || block.getId() == 425)) {
                int iterations;
                switch (block.getId()) {
                    case 12:
                    case 19:
                    case 31:
                    case 139:
                    case 161:
                    case 162:
                        iterations = 2;
                        break;
                    case 3:
                    case 24:
                    case 145:
                    case 155:
                    case 168:
                    case 179:
                        iterations = 3;
                        break;
                    case 17:
                    case 18:
                    case 98:
                        iterations = 4;
                        break;
                    case 5:
                    case 6:
                    case 97:
                    case 126:
                    case 175:
                        iterations = 6;
                        break;
                    case 1:
                        iterations = 7;
                        break;
                    case 44:
                        iterations = 8;
                        break;
                    case 38:
                        iterations = 9;
                        break;
                    case 35:
                    case 95:
                    case 159:
                    case 160:
                    case 171:
                    case 425:
                        iterations = 16;
                        break;
                    default:
                        iterations = 1;
                        break;
                }
                for (int i = 0; i < iterations; i++)
                    blocks.add(block);
            }
        }
        Material randomBlock = blocks.get((new Random()).nextInt(blocks.size()));
        int randomID = 0;
        switch (randomBlock.getId()) {
            case 12:
            case 19:
            case 31:
            case 139:
            case 161:
            case 162:
                randomID = (new Random()).nextInt(2);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 3:
            case 24:
            case 145:
            case 155:
            case 168:
            case 179:
                randomID = (new Random()).nextInt(3);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 17:
            case 18:
            case 98:
                randomID = (new Random()).nextInt(4);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 5:
            case 6:
            case 97:
            case 126:
            case 175:
                randomID = (new Random()).nextInt(6);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 1:
                randomID = (new Random()).nextInt(7);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 44:
                randomID = (new Random()).nextInt(8);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 38:
                randomID = (new Random()).nextInt(9);
                return new ItemStack(randomBlock, 1, (short) randomID);
            case 35:
            case 95:
            case 159:
            case 160:
            case 171:
            case 425:
                randomID = (new Random()).nextInt(16);
                return new ItemStack(randomBlock, 1, (short) randomID);
        }
        return new ItemStack(randomBlock, 1, (short) randomID);
    }

    public static String format(String message, String format, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            String placeholder = String.format(format, i);
            String o = String.valueOf(objects[i]);
            message = message.replace(placeholder, o);
        }

        return message;
    }
}