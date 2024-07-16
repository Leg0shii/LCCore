package de.legoshi.lccore.database.composite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PlayerCheckpointId implements Serializable {

    private String player;
    private String checkpointMap;

    public PlayerCheckpointId(String player, String checkpointMap) {
        this.player = player;
        this.checkpointMap = checkpointMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCheckpointId that = (PlayerCheckpointId) o;
        return Objects.equals(player, that.player) && Objects.equals(checkpointMap, that.checkpointMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, checkpointMap);
    }
}

