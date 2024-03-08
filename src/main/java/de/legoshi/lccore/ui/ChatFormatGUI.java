package de.legoshi.lccore.ui;

import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.ColorHelper;
import de.legoshi.lccore.util.ItemBuilder;
import de.legoshi.lccore.util.Menu;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Injector;

import java.util.ArrayList;
import java.util.List;

// PARTLY REFACTORED
public class ChatFormatGUI extends Menu {

    private static final ItemBuilder FORMAT_ON = (new ItemBuilder(Material.INK_SACK, 10)).setName("&aEnabled");
    private static final ItemBuilder FORMAT_OFF = (new ItemBuilder(Material.INK_SACK, 8)).setName("&cDisabled");
    private static final ItemStack RESET = (new ItemBuilder(Material.BARRIER)).setName("&cReset Formats").build();
    private static final String[] FORMATS = "klmno".split("");
    private static final String[] FORMATNAMES = new String[]{"Random", "Bold", "Strikethrough", "Underline", "Italic"};
    private PlayerManager playerManager;
    private Injector injector;

    public ChatFormatGUI(Player p, Injector injector) {
        super("&8Chat Format", 3);
        this.injector = injector;
        this.playerManager = injector.getInstance(PlayerManager.class);
        List<String> formatList = new ArrayList<>();
        for (ColorHelper.ChatFormat formats : ColorHelper.getChatFormats(p))
            formatList.add(formats.getCode());
        for (int i = 0; i < 5; i++) {
            int slot = i + 11;
            try {
                if (!formatList.contains(FORMATS[i])) {
                    this.inv.setItem(slot, FORMAT_OFF.setLore("", "&7&" + FORMATS[i] + FORMATNAMES[i], "", "&a&l+ &7Click to Enable").build());
                } else {
                    this.inv.setItem(slot, FORMAT_ON.setLore("", "&7&" + FORMATS[i] + FORMATNAMES[i], "", "&c&l- &7Click to Disable").build());
                }
            } catch (IndexOutOfBoundsException e) {
                this.inv.setItem(slot, new ItemStack(Material.AIR));
            }
        }
        this.inv.setItem(26, RESET);
    }

    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item.getType() == Material.INK_SACK) {
            String type = item.getItemMeta().getLore().get(1).substring(0, 4).substring(3);
            if (p.hasPermission("lc.chat." + type) && p.hasPermission("lc.chatformat")) {
                if (item.getDurability() == 8) {
                    ColorHelper.addChatFormat(p, ColorHelper.ChatFormat.fromCode(type));
                    playerManager.updatePlayer(p);
                    p.sendMessage(Utils.chat("&aYour chat format has added: &" + type + "this"));
                    p.openInventory((new ChatFormatGUI(p, injector)).getInventory());
                } else if (item.getDurability() == 10) {
                    ColorHelper.removeChatFormat(p, ColorHelper.ChatFormat.fromCode(type));
                    playerManager.updatePlayer(p);
                    p.sendMessage(Utils.chat("&cYour chat format has removed: &" + type + "this"));
                    p.openInventory((new ChatFormatGUI(p, injector)).getInventory());
                }
            } else {
                p.sendMessage(Utils.chat("&cYou don't have permission for &" + type + "this&c chatformat"));
            }
        } else if (item.isSimilar(RESET)) {
            ColorHelper.resetChatFormats(p);
            p.sendMessage(Utils.chat("&cCleared all chat formats"));
            p.closeInventory();
        }
    }
}