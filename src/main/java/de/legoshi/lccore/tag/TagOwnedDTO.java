package de.legoshi.lccore.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

@Data
@AllArgsConstructor
public class TagOwnedDTO {
    private String uuid;
    private String name;
    private ItemStack skull;
    private Date date;
}
