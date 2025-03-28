package de.legoshi.lccore.listener;

import de.legoshi.lccore.listener.events.MapChangeEvent;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.menu.maps.LCMap;
import de.legoshi.lccore.util.MapType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class MapChangeListener implements Listener {

    @Inject private CheckpointManager checkpointManager;

    @EventHandler
    public void onMapChange(MapChangeEvent event) {
        Player player = event.getPlayer();
        LCMap map = event.getToMap();

        if(map == null || !map.getMapType().equals(MapType.BONUS)) {
            checkpointManager.exitCheckpointMode(player);
        } else {
            checkpointManager.giveCheckpointGroup(player);
            checkpointManager.setCheckpointMap(player, map.getId());
        }
    }
}
