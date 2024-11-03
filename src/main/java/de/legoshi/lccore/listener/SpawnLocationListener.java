package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import team.unnamed.inject.Inject;

import java.io.IOException;

public class SpawnLocationListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onSpawn(PlayerSpawnLocationEvent e) {
        try {
            String uuid = e.getPlayer().getUniqueId().toString();
            Location loginLoc = e.getSpawnLocation();
            Location l = playerManager.getOTPLocation(uuid);
            playerManager.deleteCurrentOTPLocation(uuid);
            if(l != null) {
                e.setSpawnLocation(l);
            } else {
                // Done to reduce instances of players getting stuck in the floor
                Linkcraft.syncLater(() -> {
                    if(e.getPlayer().isOnline()) {
                        e.getPlayer().teleport(loginLoc);
                    }
                }, 10L);
            }
        } catch (IOException ignored) {}
    }
}
