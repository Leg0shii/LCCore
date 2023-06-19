package de.legoshi.lccore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

// PARTLY REFACTORED
public class LocationHelper {
    public static String getStringFromLocation(Location l) {
        return (l == null) ? "" : (l.getWorld().getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ() + ":" + l.getYaw() + ":" + l.getPitch());
    }
    public static Location getLocationFromString(String s) {
        if (s != null && !s.trim().equals("")) {
            String[] parts = s.split(":");
            if (parts.length == 6) {
                World w = Bukkit.getServer().getWorld(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                float yaw = Float.parseFloat(parts[4]);
                float pitch = Float.parseFloat(parts[5]);
                return new Location(w, x, y, z, yaw, pitch);
            }
            return null;
        }
        return null;
    }
}

