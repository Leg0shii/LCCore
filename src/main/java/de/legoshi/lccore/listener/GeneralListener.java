package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

// PARTLY REFACTORED
public class GeneralListener implements Listener {

    private List<Material> bannedItems = null;

    private void initBannedItems() {
        bannedItems = new ArrayList<>();
        for(String item : Linkcraft.getPlugin().getConfig().getStringList("banned-items")) {
            bannedItems.add(Material.matchMaterial(item));
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof Menu) {
            e.setCancelled(true);
            ((Menu) e.getClickedInventory().getHolder()).onClick(e);
        }
    }

    @EventHandler
    public void antiFlow(BlockFromToEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(bannedItems == null) {
            initBannedItems();
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(item != null) {
            Material mat = item.getType();
            if(!player.hasPermission("lc.banneditems") && bannedItems.contains(mat)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            if (!player.hasPermission("lc.banneditems")) event.setCancelled(true);
        }
    }
}