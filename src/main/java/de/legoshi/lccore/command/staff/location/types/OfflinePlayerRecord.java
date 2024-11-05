package de.legoshi.lccore.command.staff.location.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

@AllArgsConstructor
@Data
public class OfflinePlayerRecord implements LocationManageRecord{
    private final String uuid;
    private final String username;
    private final Location currentLocation;
    private final Location practiceLocation;

    @Override
    public LocationManageRecordType getType() {
        return LocationManageRecordType.OFFLINE;
    }
}
