package de.legoshi.lccore.database.composite;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PlayerChatColorId implements Serializable {

    private String player;
    private String color;

    public PlayerChatColorId(String player, String color) {
        this.player = player;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerChatColorId that = (PlayerChatColorId) o;
        return Objects.equals(player, that.player) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, color);
    }
}
