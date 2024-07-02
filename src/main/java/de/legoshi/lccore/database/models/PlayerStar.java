package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.database.composite.PlayerStarId;
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
@IdClass(PlayerStarId.class)
@Table(name = "lc_player_stars")
public class PlayerStar implements Identifiable<PlayerStarId> {
    @Id
    @Column(columnDefinition = "VARCHAR(100)")
    private String star;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    public PlayerStar(LCPlayerDB player, String star) {
        this.player = player;
        this.star = star;
    }

    public PlayerStarId getId() {
        return new PlayerStarId(player.getId(), star);
    }
}
