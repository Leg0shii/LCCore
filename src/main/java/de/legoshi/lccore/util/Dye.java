package de.legoshi.lccore.util;

public enum Dye {
    WHITE((short)0),
    ORANGE((short)1),
    MAGENTA((short)2),
    LIGHT_BLUE((short)3),
    YELLOW((short)4),
    LIME((short)5),
    PINK((short)6),
    GRAY((short)7),
    LIGHT_GRAY((short)8),
    CYAN((short)9),
    PURPLE((short)10),
    BLUE((short)11),
    BROWN((short)12),
    GREEN((short)13),
    RED((short)14),
    BLACK((short)15);

    public final short data;
    private Dye(short data) {
        this.data = data;
    }
}