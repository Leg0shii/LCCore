package de.legoshi.lccore.tag;

import de.legoshi.lccore.util.CommonUtil;
import org.bukkit.ChatColor;

public enum TagType {
    HIDDEN(ChatColor.LIGHT_PURPLE),
    VICTOR(ChatColor.GOLD),
    EVENT(ChatColor.RED),
    SPECIAL(ChatColor.DARK_AQUA);

    public final ChatColor color;
    public final String name;

    TagType(ChatColor color) {
        this.color = color;
        this.name = CommonUtil.capatalize(this.name().toLowerCase());
    }

    public static ChatColor toColor(TagType tagType) {
        return tagType.color;
    }
}
