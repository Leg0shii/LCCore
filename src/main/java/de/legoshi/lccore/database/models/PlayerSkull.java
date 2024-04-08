package de.legoshi.lccore.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Cacheable
@Table(name = "lc_player_skulls")
public class PlayerSkull {
    @Id
    @Column(length = 36)
    private String id;

    @Column(columnDefinition = "TEXT")
    private String skull;
}
