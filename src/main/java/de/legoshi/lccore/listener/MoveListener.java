package de.legoshi.lccore.listener;

import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class MoveListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!player.hasPermission("lc.banneditems")) {
            ItemStack boots = player.getInventory().getBoots();
            if(boots != null && boots.containsEnchantment(Enchantment.DEPTH_STRIDER)) {
                player.getInventory().setBoots(null);
                player.getWorld().dropItemNaturally(player.getLocation(), boots);
                MessageUtil.send(Message.DEPTH_STRIDER, player);
            }
        }
    }
}
