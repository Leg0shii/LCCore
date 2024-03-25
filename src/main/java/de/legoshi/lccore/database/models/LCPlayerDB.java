package de.legoshi.lccore.database.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lc_players")
@Cacheable
public class LCPlayerDB {
    @Id
    @Column(name = "id", length = 36)
    private String id;

    private String name;


    public LCPlayerDB(String uuid, String name) {
        this.id = uuid;
        this.name = name;
    }

    public LCPlayerDB(String uuid) {
        this.id = uuid;
        this.name = "";
    }
}
