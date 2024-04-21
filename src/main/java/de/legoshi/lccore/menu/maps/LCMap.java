package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.util.MapType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LCMap {
    private String name;
    private String id;
    private String creator;
    private double star;
    private double pp;
    private String length;
    private ItemStack item;
    private MapType mapType;
}
