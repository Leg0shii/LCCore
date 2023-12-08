package de.legoshi.lccore.menu;

import de.legoshi.lccore.manager.MapManager;

import java.util.Comparator;

public class MapStarComparator implements Comparator<LCMap> {

    @Override
    public int compare(LCMap lcMap1, LCMap lcMap2) {
        return (int) ((lcMap1.star_rating - lcMap2.star_rating) * 100);
    }
}
