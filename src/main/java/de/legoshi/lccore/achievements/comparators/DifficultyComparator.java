package de.legoshi.lccore.achievements.comparators;

import de.legoshi.lccore.achievements.Achievement;

import java.util.Comparator;

public class DifficultyComparator implements Comparator<Achievement> {

    @Override
    public int compare(Achievement ach1, Achievement ach2) {
        int rarityComparison = Integer.compare(ach1.getDifficulty().ordinal(), ach2.getDifficulty().ordinal());
        if (rarityComparison != 0) {
            return rarityComparison;
        }

        return rarityComparison;
    }
}

