package de.legoshi.lccore.menu;

import de.legoshi.lccore.manager.MapManager;

import java.util.Comparator;

public class MapStarComparator implements Comparator<LCMap> {

    @Override
    public int compare(LCMap lcMap1, LCMap lcMap2) {
        return Double.compare(lcMap1.pp, lcMap2.pp);
    }
}
