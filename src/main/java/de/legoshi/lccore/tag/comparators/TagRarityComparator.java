package de.legoshi.lccore.tag.comparators;

import de.legoshi.lccore.tag.TagDTO;

import java.util.Comparator;

public class TagRarityComparator implements Comparator<TagDTO> {
    @Override
    public int compare(TagDTO tag1, TagDTO tag2) {
        int rarityComparison = Integer.compare(tag2.getTag().getRarity().ordinal(), tag1.getTag().getRarity().ordinal());
        if (rarityComparison != 0) {
            return rarityComparison;
        }

        int ownedCountComparison = Long.compare(tag1.getOwnedCount(), tag2.getOwnedCount());
        if (ownedCountComparison != 0) {
            return ownedCountComparison;
        }

        if (tag1.getUnlocked() != null && tag2.getUnlocked() == null) {
            return -1;
        } else if (tag1.getUnlocked() == null && tag2.getUnlocked() != null) {
            return 1;
        } else {
            return 0;
        }
    }
}
