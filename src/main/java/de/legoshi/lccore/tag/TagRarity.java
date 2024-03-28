package de.legoshi.lccore.tag;

import de.legoshi.lccore.util.CommonUtil;
import org.bukkit.ChatColor;
// §
public enum TagRarity {
    COMMON(ChatColor.GRAY, ""),
    UNCOMMON(ChatColor.BLUE, ""),
    RARE(ChatColor.AQUA, "§3§l●§bRare§3§l●"),
    EPIC(ChatColor.DARK_PURPLE, "§d§l♦§5§lEpic§d§l♦"),
    LEGENDARY(ChatColor.GOLD, "§b§l★§6§lLegendary§b§l★"),
    CUSTOM(ChatColor.GREEN, "");

    public final String name;
    public final ChatColor color;
    public final String display;

    TagRarity(ChatColor color, String display) {
        this.color = color;
        this.name = CommonUtil.capatalize(this.name().toLowerCase());
        this.display = display.isEmpty() ? color + name : display;
    }
}
