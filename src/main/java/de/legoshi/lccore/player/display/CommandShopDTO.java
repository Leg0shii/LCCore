package de.legoshi.lccore.player.display;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CommandShopDTO {
    private String key;
    private String display;
    private String description;
    private List<String> permissions;
    private int cost;
    private int position;
}
