package de.legoshi.lccore.database.models;

import de.legoshi.lccore.database.Identifiable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.persistence.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Cacheable
@Entity
@Table(name = "lc_locations")
public class LCLocation implements Identifiable<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public LCLocation(Location l) {
        this.world = l.getWorld().getName();
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.yaw = l.getYaw();
        this.pitch = l.getPitch();
    }

    public Location toSpigot() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public Integer getId() {
        return id;
    }
}
