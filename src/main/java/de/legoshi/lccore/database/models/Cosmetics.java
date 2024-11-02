package de.legoshi.lccore.database.models;

import javax.persistence.*;

@Entity
@Table(name = "lc_cosmetics")
public class Cosmetics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_uuid", nullable = false)
    private String playerUUID;

    @Column(name = "data_entry", nullable = false)
    private String dataEntry;

    // Constructors, getters, and setters
    public Cosmetics() {}

    public Cosmetics(String playerUUID, String dataEntry) {
        this.playerUUID = playerUUID;
        this.dataEntry = dataEntry;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public String getDataEntry() {
        return dataEntry;
    }
}
