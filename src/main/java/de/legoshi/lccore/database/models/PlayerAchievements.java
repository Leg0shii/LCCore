package de.legoshi.lccore.database.models;

import javax.persistence.*;

@Entity
@Table(name = "lc_player_achievements")
public class PlayerAchievements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_uuid", nullable = false)
    private String playerUUID;

    @Column(name = "data_entry", nullable = false)
    private String dataEntry;

    public PlayerAchievements(String playerUUID, String dataEntry) {
        this.playerUUID = playerUUID;
        this.dataEntry = dataEntry;
    }

}
