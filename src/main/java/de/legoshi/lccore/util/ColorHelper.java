package de.legoshi.lccore.util;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// REFACTORED
public class ColorHelper {

    private static File file;
    private static FileConfiguration config;

    public static void load() {
        file = new File(Linkcraft.getInstance().getDataFolder(), "chatcolors.yml");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ChatColor getChatColor(Player p) {
        if (config.getString(p.getUniqueId().toString() + ".color") == null)
            config.set(p.getUniqueId().toString() + ".color", ChatColor.WHITE.getCode());
        return ChatColor.fromCode(config.getString(p.getUniqueId().toString() + ".color"));
    }

    public static void setChatColor(Player p, ChatColor color) {
        config.set(p.getUniqueId().toString() + ".color", color.getCode());
        save();
    }

    public static List<ChatFormat> getChatFormats(Player p) {
        List<ChatFormat> formatList = new ArrayList<>();
        for (String s : config.getStringList(p.getUniqueId().toString() + ".format"))
            formatList.add(ChatFormat.fromCode(s));
        return formatList;
    }

    public static void addChatFormat(Player p, ChatFormat format) {
        List<String> formatList = config.getStringList(p.getUniqueId().toString() + ".format");
        formatList.add(format.getCode());
        config.set(p.getUniqueId().toString() + ".format", formatList);
        save();
    }

    public static void removeChatFormat(Player p, ChatFormat format) {
        List<String> formatList = config.getStringList(p.getUniqueId().toString() + ".format");
        formatList.remove(format.getCode());
        config.set(p.getUniqueId().toString() + ".format", formatList);
        save();
    }

    public static void resetChatFormats(Player p) {
        config.set(p.getUniqueId().toString() + ".format", null);
        save();
    }

    public enum ChatColor {
        BLACK("0"),
        DARK_BLUE("1"),
        GREEN("2"),
        CYAN("3"),
        RED("4"),
        PURPLE("5"),
        ORANGE("6"),
        LIGHT_GRAY("7"),
        GRAY("8"),
        BLUE("9"),
        LIGHT_GREEN("a"),
        LIGHT_BLUE("b"),
        LIGHT_RED("c"),
        PINK("d"),
        YELLOW("e"),
        WHITE("f");

        private final String code;

        ChatColor(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public static ChatColor fromCode(String code) {
            byte b;
            int i;
            ChatColor[] arrayOfChatColors;
            for (i = (arrayOfChatColors = values()).length, b = 0; b < i; ) {
                ChatColor color = arrayOfChatColors[b];
                if (color.getCode().equals(code))
                    return color;
                b = (byte)(b + 1);
            }
            return null;
        }
    }

    public enum ChatFormat {
        RANDOM("k"),
        BOLD("l"),
        STRIKETHROUGH("m"),
        UNDERLINE("n"),
        ITALIC("o"),
        RESET("r");

        private final String code;

        ChatFormat(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }

        public static ChatFormat fromCode(String code) {
            byte b;
            int i;
            ChatFormat[] arrayOfChatFormats;
            for (i = (arrayOfChatFormats = values()).length, b = 0; b < i; ) {
                ChatFormat format = arrayOfChatFormats[b];
                if (format.getCode().equals(code))
                    return format;
                b = (byte)(b + 1);
            }
            return null;
        }
    }
}
