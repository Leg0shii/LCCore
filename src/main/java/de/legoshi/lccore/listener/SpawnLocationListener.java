package de.legoshi.lccore.listener;

import com.earth2me.essentials.Essentials;
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
            Location l = playerManager.getOTPLocation(uuid);
            playerManager.deleteCurrentOTPLocation(uuid);
            if(l != null) {
                e.setSpawnLocation(l);
            }
        } catch (IOException ignored) {}
    }
}
