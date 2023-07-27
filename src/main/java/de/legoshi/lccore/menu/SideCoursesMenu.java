package de.legoshi.lccore.menu;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.util.ContentsHelper;
import de.legoshi.lccore.util.Utils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SideCoursesMenu implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("main")
            .provider(new SideCoursesMenu())
            .size(6, 9)
            .title(Utils.chat("&lSide Courses"))
            .manager(Linkcraft.getInstance().im)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ContentsHelper ch = new ContentsHelper(inventoryContents);

        ch.createBlackSides();
        ch.createBarLine(0);
        ch.createBarLine(4);

        Pagination pagination = inventoryContents.pagination();
        ClickableItem[] items = MapManager.getMapItems();

        // I guess we just repeatedly run this everytime
        pagination.setItems(items);
        pagination.setItemsPerPage(21);

        ClickableItem[] pageItems = pagination.getPageItems();

        // Populate the inventory with the items
        for (int i = 0; i < pageItems.length; i++) {
            inventoryContents.set(i / 7 + 1, i % 7 + 1, pageItems[i]);
        }

        ch.addClickable(5, 0, Material.ARROW, "Previous Page", e -> INVENTORY.open(player, pagination.previous().getPage()));
        ch.addClickable(5, 8, Material.ARROW, "Next Page", e -> INVENTORY.open(player, pagination.next().getPage()));
        ch.addClickable(5, 4, Material.IRON_INGOT, "&cBack", e -> MapsMenu.INVENTORY.open(player));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
