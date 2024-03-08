package de.legoshi.lccore.player.chat;

import org.bukkit.ChatColor;

public enum ChatChannel {
    DIRECT(new String[]{}, "", ""),
    GLOBAL(new String[]{"a", "all"}, "ALL", ""),
    PARTY(new String[]{"p", "party"}, "PARTY", ChatColor.BLUE + "Party"),
    STAFF(new String[]{"s", "staff"}, "STAFF", ChatColor.GOLD + "@Staff");

    private final String[] matches;
    public final String display;
    public final String prefix;

    ChatChannel(String[] matches, String display, String prefix) {
        this.matches = matches;
        this.display = display;
        this.prefix = prefix;
    }

    public static ChatChannel byStr(String match) {
        for (ChatChannel channel : values()) {
            for (String value : channel.matches) {
                if (value.equalsIgnoreCase(match)) {
                    return channel;
                }
            }
        }

        return null;
    }
}
