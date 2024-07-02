package de.legoshi.lccore.database.composite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PlayerStarId implements Serializable {

    private String player;
    private String star;

    public PlayerStarId(String player, String star) {
        this.player = player;
        this.star = star;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerStarId that = (PlayerStarId) o;
        return Objects.equals(player, that.player) && Objects.equals(star, that.star);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, star);
    }
}
