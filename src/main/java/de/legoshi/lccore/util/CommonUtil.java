package de.legoshi.lccore.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public interface CommonUtil {
    Pattern regexNumber = Pattern.compile("-?\\d+(\\.\\d+)?");

    static String capatalize(String string) {
        string = string.toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    static boolean isNumeric(String string) {
        return regexNumber.matcher(string).matches();
    }

    static double toTicks(int seconds) {
        return seconds * 20;
    }

    static String formatTicksToTime(long ticks) {
        long milliseconds = (ticks * 50); // Convert ticks to milliseconds
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        milliseconds %= 1000;
        seconds %= 60;
        minutes %= 60;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    static String formatTicksToTimeItem(long ticks) {
        long seconds = ticks / 20;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        StringBuilder formattedTime = new StringBuilder();

        if (days > 0) {
            formattedTime.append(days).append("d ");
        }
        if (hours > 0) {
            formattedTime.append(hours).append("h ");
        }
        if (minutes > 0) {
            formattedTime.append(minutes).append("m ");
        }
        if (seconds > 0) {
            formattedTime.append(seconds).append("s");
        }
        if(formattedTime.length() == 0) {
            formattedTime.append("0s");
        }
        return formattedTime.toString().trim();
    }

//    static String joinArguments(ArgumentStack as) {
//        StringBuilder sb = new StringBuilder();
//        if(as.hasNext()) {
//            sb.append(as.next());
//            while (as.hasNext()) {
//                sb.append(" ").append(as.next());
//            }
//        }
//        return sb.toString();
//    }
//
//    static String joinArguments(String first, ArgumentStack as) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(first);
//        if(as.hasNext()) {
//            sb.append(" ").append(as.next());
//            while (as.hasNext()) {
//                sb.append(" ").append(as.next());
//            }
//        }
//        return sb.toString();
//    }

    static String format(String message, String format, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            String placeholder = String.format(format, i);
            String o = String.valueOf(objects[i]);
            message = message.replace(placeholder, o);
        }

        return message;
    }

    static String hqlOrderByEnum(Class<? extends Enum<?>> toOrder) {
        Enum<?>[] entries = toOrder.getEnumConstants();
        StringBuilder sb = new StringBuilder();
        for(int i = entries.length - 1; i >= 0; i--) {
            sb.append("'").append(entries[i].name()).append("',");
        }

        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    static String dateToISO(Date date) {
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

//    static List<String> stackAsList(ArgumentStack as) {
//        List<String> list = new ArrayList<>();
//
//        while(as.hasNext()) {
//            list.add(as.next());
//        }
//
//        return list;
//    }

    static <K, V> HashMap<K, V> deepCopyMap(Map<K, V> original) {
        return new HashMap<>(original);
    }

    static String locationToString(Location location) {
        return location.getWorld().getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ();
    }

    static Collection<? extends Player> removeOne(Collection<? extends Player> players, Player player) {
        Collection<Player> modified = new ArrayList<>();
        for(Player p : players) {
            if(!p.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                modified.add(p);
            }
        }

        return modified;
    }
}
