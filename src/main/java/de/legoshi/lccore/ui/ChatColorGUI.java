package de.legoshi.lccore.ui;

import de.legoshi.lccore.util.ColorHelper;
import de.legoshi.lccore.util.ItemBuilder;
import de.legoshi.lccore.util.Menu;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

// PARTLY REFACTORED
public class ChatColorGUI extends Menu {

    private static final int[] COLORIDS = new int[]{15, 11, 13, 9, 14, 10, 1, 8, 7, 11, 5, 3, 2, 6, 4, 0};
    private static final String[] COLORS = "0123456789abcdef".split("");
    private static final String[] COLORNAMES = new String[]{"Black", "Dark Blue", "Green", "Cyan", "Red", "Purple", "Orange", "Light Gray", "Gray", "Blue", "Light Green", "Light Blue", "Light Red", "Pink", "Yellow", "White"};

    public ChatColorGUI(Player p) {
        this(p, 0);
    }

    public ChatColorGUI(Player p, int page) {
        super("&8Chat Color", 5, 0);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                int slot = i + j * 9 + 10;
                int k = i + j * 7 + page * 21;
                try {
                    ItemBuilder colors = (new ItemBuilder(Material.WOOL, COLORIDS[k])).setName("&" + COLORS[k] + COLORNAMES[k]).setLore("", "&a&l+ &7Click to Enable");
                    if (ColorHelper.getChatColor(p).getCode().equals(colors.build().getItemMeta().getDisplayName().substring(0, 2).substring(1)))
                        colors.addEnchant(Enchantment.DURABILITY, 1).addFlags(ItemFlag.HIDE_ENCHANTS).setLore("", "&e&l! &7Currently Using");
                    this.inv.setItem(slot, colors.build());
                } catch (IndexOutOfBoundsException e) {
                    this.inv.setItem(slot, new ItemStack(Material.AIR));
                }
            }
        }
    }

    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item.getType() == Material.WOOL) {
            String type = item.getItemMeta().getDisplayName().substring(0, 2).substring(1);
            if (p.hasPermission("lc.chat." + type) && p.hasPermission("lc.chatcolor")) {
                ColorHelper.setChatColor(p, ColorHelper.ChatColor.fromCode(type));
                p.sendMessage(Utils.chat("&aYour chatcolor has been set to: &" + type + "this"));
                p.closeInventory();
            } else {
                p.sendMessage(Utils.chat("&cYou don't have permission for &" + type + "this &cchatcolor."));
            }
        }
    }
}