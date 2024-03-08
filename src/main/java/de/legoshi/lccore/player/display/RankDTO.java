package de.legoshi.lccore.player.display;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankDTO {
    private String key;
    private String display;
    private String tabDisplay;
    private String colorCode;
    private int position;
}
