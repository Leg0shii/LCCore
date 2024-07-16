package de.legoshi.lccore.listener;

import de.legoshi.lccore.listener.events.MapChangeEvent;
import de.legoshi.lccore.manager.CheckpointManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class MapChangeListener implements Listener {

    @Inject private CheckpointManager checkpointManager;

    @EventHandler
    public void onMapChange(MapChangeEvent event) {
        checkpointManager.exitCheckpointMode(event.getPlayer());
    }
}
