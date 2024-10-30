package de.legoshi.lccore.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

@Data
@AllArgsConstructor
public class MapCompletedDTO {
    private String uuid;
    private String name;
    private Integer completions;
    private ItemStack skull;
    private Date firstCompletion;
}
