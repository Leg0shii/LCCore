package de.legoshi.lccore.util.svs;

public enum SignClickType {
    RIGHT(2),
    LEFT(1),
    BOTH(0);

    public final int value;

    SignClickType(int clickType) {
        this.value = clickType;
    }
}
