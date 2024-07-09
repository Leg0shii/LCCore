package de.legoshi.lccore.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Sound;

@Data
@AllArgsConstructor
public class Audio {
    private Sound sound;
    private float volume;
    private float pitch;
}
