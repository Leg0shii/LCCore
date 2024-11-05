package de.legoshi.lccore.command.staff.location.types;

import de.legoshi.lccore.player.PlayerSave;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveLocationRecord implements LocationManageRecord {
    private final String uuid;
    private final String username;
    private final PlayerSave save;

    @Override
    public LocationManageRecordType getType() {
        return LocationManageRecordType.SAVE;
    }
}
