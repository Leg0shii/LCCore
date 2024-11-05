package de.legoshi.lccore.command.staff.location.types;

import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.UUID;

@Data
public class OnlinePlayerRecord implements LocationManageRecord {
    @Inject private PracticeManager practiceManager;
    @Inject private PlayerManager playerManager;
    private String uuid;
    private String username;

    public OnlinePlayerRecord init(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        return this;
    }

    public Location getCurrentLocation() {
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if(player.isOnline()) {
            return player.getLocation();
        } else {
            try {
                return playerManager.getOTPLocation(this.uuid);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public Location getPracticeLocation() {
        return practiceManager.getPracticeLocation(uuid);
    }

    @Override
    public LocationManageRecordType getType() {
        return LocationManageRecordType.ONLINE;
    }
}
