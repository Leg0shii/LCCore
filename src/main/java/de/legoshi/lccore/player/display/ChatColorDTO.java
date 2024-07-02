package de.legoshi.lccore.player.display;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class ChatColorDTO {
    private String key;
    private String code;
    private int cost;
    private int position;
    private ItemStack item;
}
