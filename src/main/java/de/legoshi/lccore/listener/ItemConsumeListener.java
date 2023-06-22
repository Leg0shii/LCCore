package de.legoshi.lccore.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ItemConsumeListener implements Listener {
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if(!player.hasPermission("lc.potion") && e.getItem().getType().equals(Material.POTION)) {
            e.setCancelled(true);
        }
    }
}
