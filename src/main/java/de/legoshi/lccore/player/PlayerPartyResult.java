package de.legoshi.lccore.player;

import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class PlayerPartyResult {
    private Player player;
    private String playerId;

    public PlayerPartyResult(Player player) {
        this.player = player;
        this.playerId = null;
    }

    public PlayerPartyResult(String playerId) {
        this.player = null;
        this.playerId = playerId;
    }

    public String getId() {
        return player != null ? player.getUniqueId().toString() : playerId;
    }

    public boolean isOnline() {
        return player != null;
    }
}
