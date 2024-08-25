package de.legoshi.lccore.achievements;

import org.bukkit.ChatColor;

public enum AchievementDifficulty {

    EASY("&2✢ &a&lEASY &2✢", ChatColor.GREEN),
    MEDIUM("&6✫ &e&lMEDIUM &6✫", ChatColor.GOLD),
    HARD("&4✳ &c&lHARD &4✳", ChatColor.RED),
    EXTREME("&d❈ &5&lEX&d&lTRE&5&lME &d❈", ChatColor.DARK_PURPLE),
    INSANE("&4&k█ &4&lIN&c&lSA&4&lNE&4&k █", ChatColor.DARK_RED);

    public String getAchievementDifficulty() {
        return achievementDifficulty;
    }

    public ChatColor getColor() {
        return color;
    }

    final String achievementDifficulty;
    ChatColor color;

    AchievementDifficulty(String achievementDifficulty, ChatColor color) {
        this.achievementDifficulty = achievementDifficulty;
        this.color = color;
    }

}
