package de.legoshi.lccore.cosmetics;

public enum CosmeticRarity {
    COMMON("Common", 1),
    UNCOMMON("Uncommon", 2),
    RARE("Rare", 3),
    EPIC("Epic", 4),
    LEGENDARY("Legendary", 5);

    private final String displayName;
    private final int rarityLevel;

    CosmeticRarity(String displayName, int rarityLevel) {
        this.displayName = displayName;
        this.rarityLevel = rarityLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRarityLevel() {
        return rarityLevel;
    }

    /**
     * Gets a CosmeticRarity by display name.
     * @param displayName the name of the rarity as a String
     * @return the corresponding CosmeticRarity, or null if not found
     */
    public static CosmeticRarity fromDisplayName(String displayName) {
        for (CosmeticRarity rarity : CosmeticRarity.values()) {
            if (rarity.displayName.equalsIgnoreCase(displayName)) {
                return rarity;
            }
        }
        return null;
    }

    /**
     * Checks if this rarity is rarer than another.
     * @param other the other CosmeticRarity to compare to
     * @return true if this rarity is higher than the other
     */
    public boolean isRarerThan(CosmeticRarity other) {
        return this.rarityLevel > other.rarityLevel;
    }

    /**
     * Returns a color or symbol associated with each rarity level.
     * Example: LEGENDARY could have a gold color.
     * @return a String representing the rarity color
     */
    public String getColorCode() {
        switch (this) {
            case COMMON: return "§7";       // Gray
            case UNCOMMON: return "§a";     // Green
            case RARE: return "§9";         // Blue
            case EPIC: return "§5";         // Purple
            case LEGENDARY: return "§6";    // Gold
            default: return "§f";           // White (default)
        }
    }

    @Override
    public String toString() {
        return getColorCode() + displayName;
    }
}
