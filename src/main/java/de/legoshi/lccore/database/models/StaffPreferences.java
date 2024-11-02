package de.legoshi.lccore.database.models;

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
@Table(name = "lc_staff_preferences")
public class StaffPreferences {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "disable_fly_on_join")
    private boolean disableFlyOnJoin = false;

    @Column(name = "vanish_on_join")
    private boolean vanishOnJoin = true;

    public StaffPreferences(Player player) {
        this.id = player.getUniqueId().toString();
    }

    public StaffPreferences(String player) {
        this.id = player;
    }
}
