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
@Table(name = "lc_player_cosmetics") // Table name
public class PlayerCosmetic implements Identifiable<String> {

    @Id
    @Column(length = 36, nullable = false) // Assuming this is the player UUID
    private String playerId;

    @Column(name = "Helmet")
    private String helmet;

    @Column(name = "Chestplate")
    private String chestplate;

    @Column(name = "Leggings")
    private String leggings;

    @Column(name = "Boots")
    private String boots;

    @Column(name = "HelmetColor")
    private String helmetColor;

    @Column(name = "ChestplateColor")
    private String chestplateColor;

    @Column(name = "LeggingsColor")
    private String leggingsColor;

    @Column(name = "BootsColor")
    private String bootsColor;

    public PlayerCosmetic(String playerId, String helmet, String chestplate, String leggings, String boots, String helmetColor, String chestplateColor, String leggingsColor, String bootsColor) {
        this.playerId = playerId;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.helmetColor = helmetColor;
        this.chestplateColor = chestplateColor;
        this.leggingsColor = leggingsColor;
        this.bootsColor = bootsColor;
    }

    public PlayerCosmetic(Player player) {
        this.playerId = player.getUniqueId().toString();
    }

    public PlayerCosmetic(String player) {
        this.playerId = player;
    }

    @Override
    public String getId() {
        return playerId;
    }
}
