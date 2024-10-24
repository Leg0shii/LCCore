package de.legoshi.lccore.util;

public enum MapType {
    RANKUP("§d§m---»§2"),
    SIDE("&6«&lPARKOUR&6»"),
    BONUS("&b«&eBONUS&b»"),
    WOLF("&1&lWolf's &9&lRankup &f&m-"),
    CHALLENGE("&4«&fCHALLENGE&4»&e"),
    MAZE("&d«&lMAZE&d»"),
    MISC("&6«&lPARKOUR&6»"),
    LEGACY("&8«&7&lLEGACY&8»");


    public final String prefix;

    MapType(String prefix) {
        this.prefix = prefix;
    }
}
