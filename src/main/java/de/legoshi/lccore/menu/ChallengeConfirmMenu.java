package de.legoshi.lccore.menu;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ContentsHelper;
import de.legoshi.lccore.util.Utils;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChallengeConfirmMenu implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("main")
            .provider(new ChallengeConfirmMenu())
            .size(1, 9)
            .title(Utils.chat("&a&lConfirm"))
            .manager(Linkcraft.getPlugin().im)
            .build();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        ContentsHelper ch = new ContentsHelper(inventoryContents);
        ch.addClickable(0, 4, Material.EMERALD_BLOCK, Utils.chat("&aWarp to challenge lobby!"), e -> Bukkit.dispatchCommand(player, "warp challenge"));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
