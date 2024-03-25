package de.legoshi.lccore.tag;

import de.legoshi.lccore.util.CommonUtil;
import org.bukkit.ChatColor;

public enum TagRarity {
    COMMON(ChatColor.GRAY),
    RARE(ChatColor.DARK_AQUA),
    EPIC(ChatColor.DARK_PURPLE),
    LEGENDARY(ChatColor.GOLD),
    CUSTOM(ChatColor.GREEN);

    public final String name;
    public final ChatColor color;

    TagRarity(ChatColor color) {
        this.color = color;
        this.name = CommonUtil.capatalize(this.name().toLowerCase());
    }
}
