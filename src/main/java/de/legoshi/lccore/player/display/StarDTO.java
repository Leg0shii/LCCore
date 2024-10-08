package de.legoshi.lccore.player.display;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class StarDTO {
    private String key;
    private String display;
    private int cost;
    private int position;
    private ItemStack item;
    private String color;
}
