package de.legoshi.lccore.player.checkpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.YamlConfiguration;

@Data
@AllArgsConstructor
public class CheckpointDTO {
    public CheckpointDTO(YamlConfiguration config) {
        this.name = config.getString("Name");
        this.type = config.getString("Type");
    }

    private String name;
    private String type;
}
