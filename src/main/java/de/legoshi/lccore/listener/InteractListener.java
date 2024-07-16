package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.player.practice.PracticeItem;
import de.legoshi.lccore.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.io.File;

public class InteractListener implements Listener {
    @Inject private PracticeManager practiceManager;
    @Inject private CheckpointManager checkpointManager;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            onRightClick(e);
        }
    }

    private void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack held = player.getItemInHand();
        Block block = e.getClickedBlock();

        if(block != null) {
            onBlockClick(e, block);
        }

        if(held != null && !held.getType().equals(Material.AIR)) {
            onItemUsage(e, held);
        }
    }

    private void onBlockClick(PlayerInteractEvent e, Block b) {
        if(checkpointManager.isCheckpointSign(b)) {
            onCheckpointUsage(e, b);
        }
    }

    private void onCheckpointUsage(PlayerInteractEvent e, Block b) {
        checkpointManager.useCheckpoint(e.getPlayer(), b);
    }

    private void onItemUsage(PlayerInteractEvent e, ItemStack held) {
        if(ItemUtil.hasNbtId(held, "practice")) {
            practiceAction(e);
        }
    }

    private void practiceAction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        practiceManager.usePracticeItem(player);
        e.setCancelled(true);
    }
}
