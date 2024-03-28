package de.legoshi.lccore.tag;

import de.legoshi.lccore.database.models.Tag;
import lombok.Data;

import java.util.Date;

@Data
public class TagDTO {
    private final Tag tag;
    private final Date unlocked;
    private final Long ownedCount;

    public Long getOwnedCount() {
        return ownedCount != null ? ownedCount : 0;
    }
}
