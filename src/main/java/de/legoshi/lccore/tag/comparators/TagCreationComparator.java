package de.legoshi.lccore.tag.comparators;

import de.legoshi.lccore.tag.TagDTO;

import java.util.Comparator;

public class TagCreationComparator implements Comparator<TagDTO> {
    @Override
    public int compare(TagDTO tag1, TagDTO tag2) {
        return Integer.compare(tag1.getTag().getOrder(), tag2.getTag().getOrder());
    }
}
