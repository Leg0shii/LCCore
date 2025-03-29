package de.legoshi.lccore.listener;

import de.legoshi.lccore.util.Utils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityInteractListener implements Listener {
    @EventHandler
    public void onEntityInteract(EntityInteractEvent e) {
        Block b = e.getBlock();

        if(Utils.isPressurePlate(b)) {
            Block attachedBlock = b.getRelative(BlockFace.DOWN);
            if(!Utils.isBlockFaceSolid(attachedBlock, BlockFace.UP)) {
                e.setCancelled(true);
            }
        }
    }
}
