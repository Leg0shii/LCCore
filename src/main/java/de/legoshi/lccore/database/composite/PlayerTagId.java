package de.legoshi.lccore.database.composite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PlayerTagId implements Serializable {

    private String player;
    private String tag;

    public PlayerTagId(String player, String tag) {
        this.player = player;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerTagId that = (PlayerTagId) o;
        return Objects.equals(player, that.player) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, tag);
    }
}
