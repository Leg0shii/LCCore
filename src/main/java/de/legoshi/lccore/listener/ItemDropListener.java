package de.legoshi.lccore.listener;

import de.legoshi.lccore.util.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDropListener implements Listener {

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        ItemStack i = e.getItemDrop().getItemStack();
        if (ItemUtil.hasNbtId(i, "practice")) e.setCancelled(true);
    }
}
