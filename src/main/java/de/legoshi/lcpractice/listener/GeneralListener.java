package de.legoshi.lcpractice.listener;

import de.legoshi.lcpractice.util.ColorHelper;
import de.legoshi.lcpractice.util.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

// PARTLY REFACTORED
public class GeneralListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String chat = "";
        if (ColorHelper.getChatColor(p) != null) {
            chat = chat + "&" + ColorHelper.getChatColor(p).getCode();
        }
        for (ColorHelper.ChatFormat formats : ColorHelper.getChatFormats(p)) {
            chat = chat + "&" + formats.getCode();
        }
        e.setMessage(ChatColor.translateAlternateColorCodes('&', chat) + e.getMessage());
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
        Player player = event.getPlayer();
        if (!player.isOp() && event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getItemInHand().getType() == Material.FIREWORK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileThrownEvent(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            if (!player.isOp()) event.setCancelled(true);
        }
    }
}