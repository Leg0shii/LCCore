package de.legoshi.lccore.util;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class ContentsHelper {

    private final InventoryContents contents;

    public ContentsHelper(InventoryContents contents) {
        this.contents = contents;
    }

    public void createBarLine(int row) {
        ItemStack blue_pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
        ItemStack cyan_pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 9);
        ItemStack lightblue_pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);

        // Remove item meta from them
        ItemMeta im = blue_pane.getItemMeta();
        im.setDisplayName(" ");
        im.setLore(null);

        blue_pane.setItemMeta(im);
        cyan_pane.setItemMeta(im);
        lightblue_pane.setItemMeta(im);

        contents.fillRect(row, 0, row, 8, ClickableItem.empty(blue_pane));
        contents.fillRect(row, 2, row, 6, ClickableItem.empty(cyan_pane));
        contents.set(row, 4, ClickableItem.empty(lightblue_pane));
    }

    public void createBlackSides() {
        ItemStack black_pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);

        // Remove item meta from them
        ItemMeta im = black_pane.getItemMeta();
        im.setDisplayName(" ");
        im.setLore(null);

        black_pane.setItemMeta(im);

        contents.fillRect(0, 0, 4, 0, ClickableItem.empty(black_pane));
        contents.fillRect(0, 8, 4, 8, ClickableItem.empty(black_pane));
    }

    public void addEmpty(int row, int col, Material m, int damage) {
        ItemStack is = new ItemStack(m, 1, (short) damage);
        ItemMeta im = is.getItemMeta();

        im.setLore(null);
        is.setItemMeta(im);

        contents.set(row, col, ClickableItem.empty(is));
    }

    public void addEmpty(int row, int col, Material m) {
        addEmpty(row, col, m, 0);
    }

    public void addEmpty(int row, int col, Material m, String displayName) {
        addEmpty(row, col, m, 0, displayName);
    }

    public void addEmpty(int row, int col, Material m, int damage, String displayName) {
        ItemStack is = new ItemStack(m, 1, (short) damage);
        ItemMeta im = is.getItemMeta();

        im.setDisplayName(Utils.chat(displayName));
        im.setLore(null);
        is.setItemMeta(im);

        contents.set(row, col, ClickableItem.empty(is));
    }

    public void addClickable(int row, int col, Material m, int damage, Consumer<InventoryClickEvent> c) {
        ItemStack is = new ItemStack(m, 1, (short) damage);

        ItemMeta im = is.getItemMeta();

        im.setLore(null);
        is.setItemMeta(im);

        contents.set(row, col, ClickableItem.of(is, c));
    }

    public void addClickable(int row, int col, Material m, Consumer<InventoryClickEvent> c) {
        addClickable(row, col, m, 0, c);
    }

    public void addClickable(int row, int col, Material m, int damage, String displayName, Consumer<InventoryClickEvent> c) {
        ItemStack is = new ItemStack(m, 1, (short) damage);

        ItemMeta im = is.getItemMeta();

        im.setDisplayName(Utils.chat(displayName));
        im.setLore(null);
        is.setItemMeta(im);

        contents.set(row, col, ClickableItem.of(is, c));
    }

    public void addClickable(int row, int col, Material m, String displayName, Consumer<InventoryClickEvent> c) {
        addClickable(row, col, m, 0, displayName, c);
    }
}
