package de.legoshi.lccore.achievements;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum AchievementDifficulty {

    EASY("&2✢ &a&lEASY &2✢", ChatColor.GREEN),
    MEDIUM("&6✫ &e&lMEDIUM &6✫", ChatColor.GOLD),
    HARD("&4✳ &c&lHARD &4✳", ChatColor.RED),
    EXTREME("&d❈ &5&lEX&d&lTRE&5&lME &d❈", ChatColor.DARK_PURPLE),
    INSANE("&4&k█ &4&lIN&c&lSA&4&lNE&4&k █", ChatColor.DARK_RED);

    final String achievementDifficulty;
    final ChatColor color;

    AchievementDifficulty(String achievementDifficulty, ChatColor color) {
        this.achievementDifficulty = achievementDifficulty;
        this.color = color;
    }

    public static AchievementDifficulty byPoints(int points) {
        switch (points) {
            case 5:
                return EASY;
            case 10:
                return MEDIUM;
            case 15:
                return HARD;
            case 20:
                return EXTREME;
            case 25:
                return INSANE;
        }
        return null;
    }

}
