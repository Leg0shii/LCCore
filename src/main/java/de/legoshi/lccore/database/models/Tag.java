package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import de.legoshi.lccore.tag.TagRarity;
import de.legoshi.lccore.tag.TagType;
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
@Table(name = "lc_tags")
public class Tag implements Identifiable<String> {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
    @Id
    @Column(length = 100)
    private String name = "";

    @Column(length = 150)
    private String display = "";

//    private String description = "";

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private TagRarity rarity = TagRarity.COMMON;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private TagType type = TagType.HIDDEN;

    private boolean obtainable = true;

    private boolean visible = true;

//    @CreationTimestamp
//    private Date created;

    public Tag(String id) {
        this.name = id;
    }

    public String getId() {
        return name;
    }
}
