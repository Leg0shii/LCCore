package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import team.unnamed.inject.Inject;

public class PlayerKickListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Linkcraft.async(() -> playerManager.playerLeave(event.getPlayer()));
    }
}
