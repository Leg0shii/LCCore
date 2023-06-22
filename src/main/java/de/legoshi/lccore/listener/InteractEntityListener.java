package de.legoshi.lccore.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class InteractEntityListener implements Listener {
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if(!player.hasPermission("lc.banneditems") && (item.getType().equals(Material.MONSTER_EGG) || item.getType().equals(Material.MONSTER_EGGS))) {
            event.setCancelled(true);
        }
    }
}
