package de.legoshi.lccore.listener.events;

import de.legoshi.lccore.menu.maps.LCMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class MapChangeEvent extends Event {

    private final Player player;
    private final LCMap toMap;

    public MapChangeEvent(Player player, LCMap toMap) {
        this.player = player;
        this.toMap = toMap;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
