package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;


// REFACTORED
public class PkPracListener implements Listener {

    private final FileConfiguration playerdataConfig = Linkcraft.getInstance().playerdataConfigAccessor.getConfig();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Action a = e.getAction();
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        if (a != Action.LEFT_CLICK_AIR && a != Action.LEFT_CLICK_BLOCK && a != Action.PHYSICAL && p.getItemInHand().isSimilar(Linkcraft.getInstance().getReturnItem())) {
            e.setCancelled(true);
            String saved = this.playerdataConfig.getString(uuid);
            if (saved != null) {
                Location loc = LocationHelper.getLocationFromString(saved);
                p.teleport(loc);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        Item i = e.getItemDrop();
        if (i.getItemStack().isSimilar(Linkcraft.getInstance().getReturnItem())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        FileConfiguration config = Linkcraft.getInstance().getConfig();
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        if (this.playerdataConfig.getString(uuid) != null) {
            String practiceMessage = config.getString(Constants.PRACTICE_MESSAGE);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', practiceMessage));
        }
    }
}

