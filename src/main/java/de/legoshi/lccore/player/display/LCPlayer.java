package de.legoshi.lccore.player.display;

import de.legoshi.lccore.database.models.Punishment;
import de.legoshi.lccore.util.ColorHelper;
import lombok.Data;
import org.bukkit.ChatColor;

import java.util.List;

@Data
public class LCPlayer {
    private String nick;
    private String name;
    private RankDTO rank;
    private BonusDTO bonus;
    private WolfDTO wolf;
    private MazeDTO maze;
    private StarDTO star;
    private StaffDTO staff;
    private List<ColorHelper.ChatFormat> chatFormats;
    private ColorHelper.ChatColor chatColor;
    private List<ColorHelper.ChatFormat> nameFormats;
    private List<ChatColor> manualChatColors;
    private List<ChatColor> manualChatFormats;
    private List<Punishment> punishments;
}
