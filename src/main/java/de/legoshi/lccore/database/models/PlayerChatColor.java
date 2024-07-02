package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.database.composite.PlayerChatColorId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Cacheable
@IdClass(PlayerChatColorId.class)
@Table(name = "lc_player_chat_colors")
public class PlayerChatColor implements Identifiable<PlayerChatColorId> {
    @Id
    @Column(columnDefinition = "VARCHAR(100)")
    private String color;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    public PlayerChatColor(LCPlayerDB player, String color) {
        this.player = player;
        this.color = color;
    }

    public PlayerChatColorId getId() {
        return new PlayerChatColorId(player.getId(), color);
    }
}
