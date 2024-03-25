package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "lc_player_preferences")
public class PlayerPreferences implements Identifiable<String> {
    @Id
    @Column(length = 36)
    private String id;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private Tag tag = null;

    public PlayerPreferences(Player player) {
        this.id = player.getUniqueId().toString();
    }
}
