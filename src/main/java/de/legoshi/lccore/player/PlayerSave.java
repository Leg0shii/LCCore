package de.legoshi.lccore.player;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

@Data
public class PlayerSave {
    private String key;
    private Location location;
    private String name;
    private ItemStack item;
    private Date date;
}
