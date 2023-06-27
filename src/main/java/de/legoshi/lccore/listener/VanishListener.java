package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.VisibilityManager;
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class VanishListener implements Listener {

    @Inject private VisibilityManager visibilityManager;

    @EventHandler
    public void onPlayerVanish(PlayerVanishStateChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getName());
        if(event.isVanishing()) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.getUniqueId().equals(player.getUniqueId())) {
                    visibilityManager.removeFromTabList(p, player);
                }
            }
        } else {
            visibilityManager.hideIfHiding(player);
        }
    }
}
