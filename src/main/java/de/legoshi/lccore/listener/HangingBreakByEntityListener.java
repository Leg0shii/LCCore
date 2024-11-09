package de.legoshi.lccore.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

public class HangingBreakByEntityListener implements Listener {
    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if(!(event.getRemover() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getRemover();

        if(!player.hasPermission("linkcraft.builder")) {
            event.setCancelled(true);
        }
    }
}
