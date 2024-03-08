package de.legoshi.lccore.player.display;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MazeDTO {
    private String key;
    private String tabDisplay;
    private int position;
}
