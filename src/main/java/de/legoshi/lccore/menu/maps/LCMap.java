package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.util.BanType;
import de.legoshi.lccore.util.MapType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LCMap {
    private String name;
    private String id;
    private String creator;
    private Double star;
    private Double pp;
    private Boolean ppReward;
    private String length;
    private ItemStack item;
    private MapType mapType;
    private String victoryWarp;
    private String rank;
    private List<String> tags;
    private Sound sound;
    private Float soundPitch;
    private Float soundVolume;
    private List<ItemStack> itemRewards;
    private List<String> broadcasts;
    private List<String> playerMessages;
    private BanType forceBan;
    private String display;
    private Boolean noPrac;
}
