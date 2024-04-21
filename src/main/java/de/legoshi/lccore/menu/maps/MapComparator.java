package de.legoshi.lccore.menu.maps;

import java.util.Comparator;

public class MapComparator implements Comparator<LCMap> {

    @Override
    public int compare(LCMap lcMap1, LCMap lcMap2) {
        int ppComparison = Double.compare(lcMap1.getPp(), lcMap2.getPp());

        if (ppComparison == 0) {
            return lcMap1.getName().compareTo(lcMap2.getName());
        }

        return ppComparison;
    }
}
