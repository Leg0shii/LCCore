package de.legoshi.lccore.player;

public enum PunishmentType {
    MUTE("Mute"),
    FULL_MUTE("Full Mute");

    public final String display;

    PunishmentType(String display) {
        this.display = display;
    }
}
