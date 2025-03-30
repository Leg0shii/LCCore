package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

public class ItemDropListener implements Listener {

    @Inject private PlayerManager playerManager;
    @Inject private PracticeManager practiceManager;

    // Extremely hacky, but the PlayerDropItemEvent removes the dropped item from the inventory before the event is called unfortunately
    private void removePracItemLater(Player player) {
        Linkcraft.syncLater(() -> {
            for (ItemStack item : player.getInventory().getContents()) {
                if (ItemUtil.hasNbtId(item, "practice")) {
                    player.getInventory().removeItem(item);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        ItemStack i = e.getItemDrop().getItemStack();

        if (ItemUtil.hasNbtId(i, "practice")) {
            Player player = e.getPlayer();
            PlayerPreferences prefs = playerManager.getPlayerPrefs(player);
            switch (prefs.getQuickUnpractice()) {
                case DROP:
                    practiceManager.unpractice(player);
                    removePracItemLater(player);
                    break;
                case SHIFT_DROP:
                    if(player.isSneaking()) {
                        practiceManager.unpractice(player);
                        removePracItemLater(player);
                    }
                    break;
            }
            e.setCancelled(true);
        } else if(ItemUtil.hasNbtId(i, "unpractice")) {
            e.setCancelled(true);
        }
    }
}
