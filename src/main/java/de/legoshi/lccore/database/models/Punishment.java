package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.player.PunishmentType;
import de.legoshi.lccore.tag.TagRarity;
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
@Cacheable
@Entity
@Table(name = "lc_punishments")
public class Punishment implements Identifiable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PunishmentType type;

    @CreationTimestamp
    @Column(name = "punished_date")
    private Date punishedDate;

    private Date length;

    @Column(length = 50)
    private String ip;

    @Column
    private String reason;

    public Integer getId() {
        return id;
    }
}
