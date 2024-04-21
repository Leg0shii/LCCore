package de.legoshi.lccore.database.composite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PlayerCompletionId implements Serializable {

    private String player;
    private String map;

    public PlayerCompletionId(String player, String map) {
        this.player = player;
        this.map = map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCompletionId that = (PlayerCompletionId) o;
        return Objects.equals(player, that.player) && Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, map);
    }
}
