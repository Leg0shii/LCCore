package de.legoshi.lccore.util;

public enum GUIAction {
    LEFT_CLICK("Left Click"),
    SHIFT_LEFT_CLICK("Shift + Left Click"),
    RIGHT_CLICK("Right Click"),
    SHIFT_RIGHT_CLICK("Shift + Right Click");

    public final String display;

    GUIAction(String display) {
        this.display = display;
    }
}
