package de.legoshi.lccore.player.display;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BonusDTO {
    private String key;
    private String display;
    private int position;
}
