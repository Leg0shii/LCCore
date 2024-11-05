package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

public class BlockBreakListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack held = player.getItemInHand();

        if(held != null && !held.getType().equals(Material.AIR)) {
            onItemUsage(e, held);
        }
    }

    private void onItemUsage(BlockBreakEvent e, ItemStack held) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        if(ItemUtil.hasNbtId(held, "lcwand")) {
            playerManager.setClipboardPos1(p, b.getLocation());
            e.setCancelled(true);
        }
    }
}
