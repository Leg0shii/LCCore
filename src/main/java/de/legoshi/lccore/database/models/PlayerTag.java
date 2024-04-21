package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.database.composite.PlayerTagId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Cacheable
@IdClass(PlayerTagId.class)
@Table(name = "lc_player_tags")
public class PlayerTag implements Identifiable<PlayerTagId> {
    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false, columnDefinition = "VARCHAR(100)")
    private Tag tag;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    @CreationTimestamp
    private Date unlocked;

    public PlayerTagId getId() {
        return new PlayerTagId(player.getId(), tag.getId());
    }

    public PlayerTag(Tag tag, LCPlayerDB lcPlayerDB) {
        this.tag = tag;
        this.player = lcPlayerDB;
    }
}
