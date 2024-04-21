package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Cacheable
@IdClass(PlayerCompletionId.class)
@Table(name = "lc_player_completions")
public class PlayerCompletion implements Identifiable<PlayerCompletionId> {
    @Id
    private String map;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    private int completions;

    private Date first = null;

    private Date latest = null;

    public PlayerCompletionId getId() {
        return new PlayerCompletionId(player.getId(), map);
    }
}
