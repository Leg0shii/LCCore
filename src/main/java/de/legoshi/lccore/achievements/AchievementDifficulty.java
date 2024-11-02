package de.legoshi.lccore.achievements;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum AchievementDifficulty {

    EASY("&2✢ &a&lEASY &2✢", ChatColor.GREEN),
    MEDIUM("&6✫ &e&lMEDIUM &6✫", ChatColor.GOLD),
    HARD("&4✳ &c&lHARD &4✳", ChatColor.RED),
    EXTREME("&d❈ &5&lEX&d&lTRE&5&lME &d❈", ChatColor.DARK_PURPLE),
    DIVINE("&4&k█ &4&lDI&c&lVI&4&lNE&4&k █", ChatColor.DARK_RED),
    SECRET("&d✫ &5&lSE&d&lCR&5&lET &d✫", ChatColor.DARK_PURPLE);

    final String achievementDifficulty;
    final ChatColor color;

    AchievementDifficulty(String achievementDifficulty, ChatColor color) {
        this.achievementDifficulty = achievementDifficulty;
        this.color = color;
    }

    public static AchievementDifficulty byPoints(Achievement achievement) {

        if (achievement.getType() == AchievementType.SECRET) {
            return SECRET;
        }

        int points = achievement.getPoints();

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
                return DIVINE;
        }
        return null;
    }

}
