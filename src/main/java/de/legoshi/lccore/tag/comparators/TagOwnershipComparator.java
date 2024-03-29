package de.legoshi.lccore.tag.comparators;

import de.legoshi.lccore.tag.TagDTO;

import java.util.Comparator;

public class TagOwnershipComparator implements Comparator<TagDTO> {
    @Override
    public int compare(TagDTO tag1, TagDTO tag2) {
        if (tag1.getUnlocked() != null && tag2.getUnlocked() == null) {
            return -1;
        } else if (tag1.getUnlocked() == null && tag2.getUnlocked() != null) {
            return 1;
        }

        int rarityComparison = Integer.compare(tag1.getTag().getRarity().ordinal(), tag2.getTag().getRarity().ordinal());
        if (rarityComparison != 0) {
            return rarityComparison;
        }

        return Long.compare(tag2.getOwnedCount(), tag1.getOwnedCount());
    }
}
