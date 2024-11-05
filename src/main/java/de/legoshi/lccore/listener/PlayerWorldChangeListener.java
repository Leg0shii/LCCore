package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import team.unnamed.inject.Inject;

public class PlayerWorldChangeListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        playerManager.clearClipboard(p);
    }
}
