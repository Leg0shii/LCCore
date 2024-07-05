package de.legoshi.lccore.player.chat;

public enum PartyRole {
    OWNER("§8[§bO§8]§r", 3),
    MODERATOR("§8[§9M§8]§r", 2),
    GUEST("§8[§3G§8]§r", 1);

    public final String prefix;
    public final int weight;

    PartyRole(String prefix, int weight) {
        this.prefix = prefix;
        this.weight = weight;
    }
}
