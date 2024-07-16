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
@Entity
@Table(name = "lc_players")
@Cacheable
public class LCPlayerDB implements Identifiable<String> {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    private String name;

    @Column(name = "current_checkpoint_map")
    private String currentCheckpointMap = null;

    public LCPlayerDB(String uuid, String name) {
        this.id = uuid;
        this.name = name;
    }

    public LCPlayerDB(String uuid) {
        this.id = uuid;
        this.name = "";
    }

    public LCPlayerDB(Player player) {
        this(player.getUniqueId().toString(), player.getName());
    }
}
