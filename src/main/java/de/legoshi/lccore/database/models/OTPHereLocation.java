package de.legoshi.lccore.database.models;


import de.legoshi.lccore.database.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lc_otphere_locations")
@Cacheable
public class OTPHereLocation implements Identifiable<String>, Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private LCPlayerDB player;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LCLocation location;

    @Column(name = "otp_by")
    private String otpBy = null;

    public String getId() {
        return player.getId();
    }
}
