package de.legoshi.lccore.util;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack.asNMSCopy;

public interface ItemUtil {

    ItemStack pracItem = addNbtId(setItemText(new ItemStack(Material.SLIME_BALL), "§3§l【§b§lLC§3§l】- §4Return"), "prac");

    static void addGlow(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(im);
    }

    static ItemStack setMeta(ItemStack item, ItemMeta meta) {
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack hideAttributes(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(im);
        return item;
    }

    static String getName(ItemStack itemStack) {
        if(itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if(meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return null;
    }

    static String toBase64(ItemStack item) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(output);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(output.toByteArray());
        } catch (IOException ignored) {
            return null;
        }
    }

    static ItemStack fromBase64(String base64) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(input);
            return (ItemStack)dataInput.readObject();
        } catch (IOException ignored) {
            return null;
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    static boolean isFullInventory(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    static ItemStack addNbtId(ItemStack item, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = asNMSCopy(item);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set("id", new NBTTagString(value));
        nmsItem.setTag(itemCompound);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    static void removeItemIf(Player player, Predicate<ItemStack> predicate) {
        Inventory inv = player.getInventory();
        for(ItemStack item : inv.getContents()) {
            if(item == null || item.getType().equals(Material.AIR)) continue;

            if(predicate.test(item)) {
                inv.remove(item);
            }
        }
    }

    static ItemStack setItemText(ItemStack item, String... text) {
        if (item != null && text != null && text.length > 0) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String combined = Arrays.stream(text)
                        .map(s -> s == null ? " " : s)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.joining("\n"));
                String[] lines = combined.split("\n");
                if (text[0] != null) {
                    meta.setDisplayName(lines[0]);
                }
                if (lines.length > 1) {
                    meta.setLore(Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length)));
                } else {
                    meta.setLore(null);
                }
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    static boolean hasNbtId(ItemStack item, String value) {
        if(item == null) {
            return false;
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = asNMSCopy(item);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        return itemCompound.getString("id").equals(value);
    }

    static List<Material> getMaterialsOneOfEach() {
        List<Material> blocks = new ArrayList<>();
        int[] disabledBlocks = {
                0, 8, 9, 10, 11, 26, 34, 36, 44, 51,
                55, 59, 60, 62, 63, 64, 68, 71, 74, 75,
                83, 90, 92, 93, 94, 104, 105, 115,
                117, 118, 119, 124, 125, 127, 132, 140, 141,
                142, 144, 149, 150, 176, 177, 178, 181, 193, 194, 195,
                196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206,
                207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217,
                218, 219, 220, 221, 223, 224, 225, 226, 227, 228, 229,
                230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240,
                241, 242, 243, 243, 244, 245, 246, 247, 248, 249, 250,
                251, 252, 253, 254, 255, 383, 387};

        for (Material block : Material.values()) {
            if (IntStream.of(disabledBlocks).noneMatch(x -> (x == block.getId()))) {//(block.getId() < 193 || block.getId() == 425)) {
                blocks.add(block);
            }
        }


        return blocks;
    }

    static List<ItemStack> getVariations(Material mat, int variations) {
        List<ItemStack> items = new ArrayList<>();

        for(int i = 0; i < variations; i++) {
            items.add(new ItemStack(mat, 1, (short)i));
        }

        return items;
    }

    static List<ItemStack> getVariationsAmt(Material mat, int variations, int amt) {
        List<ItemStack> items = new ArrayList<>();

        for(int i = 0; i < variations; i++) {
            items.add(new ItemStack(mat, amt, (short)i));
        }

        return items;
    }

    static List<ItemStack> getAllItems() {
        List<Material> materials = getMaterialsOneOfEach();
        List<ItemStack> items = new ArrayList<>();
        for (Material mat : materials) {
            switch (mat.getId()) {
                case 12:
                case 19:
                case 31:
                case 139:
                case 161:
                case 162:
                case 263:
                case 322:
                case 350:
                    items.addAll(getVariations(mat, 2));
                    break;
                case 3:
                case 24:
                case 145:
                case 155:
                case 168:
                case 179:
                    items.addAll(getVariations(mat, 3));
                    break;
                case 17:
                case 18:
                case 98:
                case 349:
                    items.addAll(getVariations(mat, 4));
                    break;
                case 397:
                    items.addAll(getVariations(mat, 5));
                    break;
                case 5:
                case 6:
                case 97:
                case 126:
                case 175:
                    items.addAll(getVariations(mat, 6));
                    break;
                case 1:
                    items.addAll(getVariations(mat, 7));
                    break;
                case 44:
                    items.addAll(getVariations(mat, 8));
                    break;
                case 38:
                    items.addAll(getVariations(mat, 9));
                    break;
                case 35:
                case 95:
                case 159:
                case 160:
                case 171:
                case 351:
                case 425:
                    items.addAll(getVariations(mat, 16));
                    break;
                default:
                    items.add(new ItemStack(mat, 1));
                    break;
            }
        }
        return items;
    }

    static ItemStack getRandomItem() {
        List<ItemStack> items = getAllItems();
        return items.get(new Random().nextInt(items.size()));
    }

    static ItemStack potion(PotionType type, int level, boolean extend) {
        Potion potion = new Potion(type);
        potion.setLevel(level);
        if(extend) {
            potion = potion.extend();
        }

        return potion.toItemStack(1);
    }

    static ItemStack addFlag(ItemStack item, ItemFlag flag) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flag);
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack potion(PotionType type) {
        return potion(type, 1, false);
    }

    static ItemStack potion(PotionType type, boolean hide) {
        if(hide) {
            return addFlag(potion(type), ItemFlag.HIDE_POTION_EFFECTS);
        }
        return potion(type, 1, false);
    }

    static List<ItemStack> getAllItemsAmt(int amt) {
        List<Material> materials = getMaterialsOneOfEach();
        List<ItemStack> items = new ArrayList<>();
        for (Material mat : materials) {
            switch (mat.getId()) {
                case 12:
                case 19:
                case 31:
                case 139:
                case 161:
                case 162:
                    items.addAll(getVariationsAmt(mat, 2, amt));
                    break;
                case 3:
                case 24:
                case 145:
                case 155:
                case 168:
                case 179:
                    items.addAll(getVariationsAmt(mat, 3, amt));
                    break;
                case 17:
                case 18:
                case 98:
                    items.addAll(getVariationsAmt(mat, 4, amt));
                    break;
                case 5:
                case 6:
                case 97:
                case 126:
                case 175:
                    items.addAll(getVariationsAmt(mat, 6, amt));
                    break;
                case 1:
                    items.addAll(getVariationsAmt(mat, 7, amt));
                    break;
                case 44:
                    items.addAll(getVariationsAmt(mat, 8, amt));
                    break;
                case 38:
                    items.addAll(getVariationsAmt(mat, 9, amt));
                    break;
                case 35:
                case 95:
                case 159:
                case 160:
                case 171:
                case 425:
                    items.addAll(getVariationsAmt(mat, 16, amt));
                    break;
                default:
                    items.add(new ItemStack(mat, amt));
                    break;
            }
        }
        return items;
    }
}
