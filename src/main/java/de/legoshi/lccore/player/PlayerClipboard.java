package de.legoshi.lccore.player;

import lombok.Data;
import org.bukkit.Location;

@Data
public class PlayerClipboard {
    private Location pos1 = null;
    private Location pos2 = null;

    public boolean isLocationWithin(Location l) {
        if(!l.getWorld().equals(pos1.getWorld()) || !l.getWorld().equals(pos2.getWorld())) {
            return false;
        }

        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());

        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());

        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return l.getX() >= minX && l.getX() <= maxX &&
                l.getY() >= minY && l.getY() <= maxY &&
                l.getZ() >= minZ && l.getZ() <= maxZ;
    }

    public boolean isLocationWithin(double x, double y, double z) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());

        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());

        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }
}
