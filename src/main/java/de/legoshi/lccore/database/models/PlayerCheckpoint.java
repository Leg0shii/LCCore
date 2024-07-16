package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.database.composite.PlayerCheckpointId;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
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
@IdClass(PlayerCheckpointId.class)
@Table(name = "lc_player_checkpoints")
public class PlayerCheckpoint implements Identifiable<PlayerCheckpointId> {

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    @Id
    @Column(name = "checkpoint_map")
    private String checkpointMap;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LCLocation location;

    public PlayerCheckpointId getId() {
        return new PlayerCheckpointId(player.getId(), checkpointMap);
    }
}