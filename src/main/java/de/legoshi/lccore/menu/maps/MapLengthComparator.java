package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.util.MapLength;

import java.util.Comparator;

public class MapLengthComparator implements Comparator<LCMap> {

    @Override
    public int compare(LCMap lcMap1, LCMap lcMap2) {
        Integer ordinal1 = getMapLengthOrdinal(lcMap1.getLength());
        int ordinal2 = getMapLengthOrdinal(lcMap2.getLength());

        if (ordinal1.equals(ordinal2)) {
            return new MapPPComparator().compare(lcMap1, lcMap2);
        }

        return Integer.compare(ordinal1, ordinal2);
    }

    private int getMapLengthOrdinal(String length) {
        try {
            return MapLength.valueOf(length.toUpperCase()).ordinal();
        } catch (IllegalArgumentException | NullPointerException e) {
            return Integer.MIN_VALUE;
        }
    }
}
