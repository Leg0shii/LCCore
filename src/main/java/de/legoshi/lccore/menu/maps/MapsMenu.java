package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ContentsHelper;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MapsMenu implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("main")
            .provider(new MapsMenu())
            .size(5, 9)
            .title("Menu")
            .manager(Linkcraft.getPlugin().im)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ContentsHelper ch = new ContentsHelper(inventoryContents);

        ch.createBlackSides();
        ch.createBarLine(0);
        ch.createBarLine(4);

        ch.addClickable(1, 2,  Material.GOLD_INGOT, "&e&lBonus Rankups", e -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cc open bonus " + player.getName()));
        ch.addClickable(3, 2,  Material.EMERALD, "&a&lSegmented Challenges", e -> ChallengeConfirmMenu.INVENTORY.open(player));
        ch.addClickable(2, 4,  Material.DIAMOND, "&b&lMain Rankups", e -> MainRankupConfirmMenu.INVENTORY.open(player));
        ch.addClickable(1, 6,  Material.IRON_INGOT, "&f&lSide Courses", e -> SideCoursesMenu.INVENTORY.open(player));
        ch.addClickable(3, 6,  Material.REDSTONE, "&c&lMaze Rankups", e -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cc open maze " + player.getName()));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
