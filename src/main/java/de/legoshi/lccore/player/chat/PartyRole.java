package de.legoshi.lccore.player.chat;

public enum PartyRole {
    OWNER("§8[§bO§8]§r"),
    MODERATOR("§8[§9M§8]§r"),
    GUEST("§8[§3G§8]§r");

    public final String prefix;

    PartyRole(String prefix) {
        this.prefix = prefix;
    }
}
