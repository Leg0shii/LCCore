package de.legoshi.lccore.menu;

import java.util.Comparator;

public class MapComparator implements Comparator<LCMap> {

    @Override
    public int compare(LCMap lcMap1, LCMap lcMap2) {
        int ppComparison = Double.compare(lcMap1.pp, lcMap2.pp);

        if (ppComparison == 0) {
            return lcMap1.name.compareTo(lcMap2.name);
        }

        return ppComparison;
    }
}
