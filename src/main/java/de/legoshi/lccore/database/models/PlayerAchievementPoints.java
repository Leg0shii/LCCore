package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lc_player_achievementpoints")
public class PlayerAchievementPoints implements Identifiable<String> {

    @Id
    @Column(length = 36, nullable = false) // Assuming this is the player UUID
    private String playerId;

    @Column(name = "points")
    private int points;

    public PlayerAchievementPoints(String playerId, int points) {
        this.playerId = playerId;
        this.points = points;
    }

    @Override
    public String getId() {
        return null;
    }
}
