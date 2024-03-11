package de.legoshi.lccore.util;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.StaticGuiElement;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;

public interface GUIUtil {
    static <T extends Enum<T>> GuiStateElement.State[] createSelectionMenu(Class<? extends Enum<?>> e, ItemStack item, String itemName, BiConsumer<GuiElement.Click, T> setter, boolean skipFirst, String ... after) {
        List<GuiStateElement.State> options = new ArrayList<>();
        List<String> names = enumToList(e);


        if (skipFirst) {
            names.remove(0);
        }

        for (int i = 0; i < names.size(); i++) {
            int finalI = skipFirst ? i + 1 : i;

            options.add(new GuiStateElement.State(click -> setter.accept(click, (T) e.getEnumConstants()[finalI]), e.getEnumConstants()[finalI].name(), item, getDescription(itemName, i, names, after)));
        }

        return options.toArray(new GuiStateElement.State[0]);
    }

    static <T> GuiStateElement.State[] createSelectionMenu(LinkedHashMap<String, T> names, ItemStack item, String itemName, BiConsumer<GuiElement.Click, T> setter, String ... after) {
        List<GuiStateElement.State> options = new ArrayList<>();
        ArrayList<String> displayOpts = new ArrayList<>(names.keySet());
        int i = 0;
        for(String key : names.keySet()) {
            int finalI = i;
            options.add(new GuiStateElement.State(click -> setter.accept(click, names.get(key)), key, item, getDescription(itemName, finalI, displayOpts, after)));
            i++;
        }

        return options.toArray(new GuiStateElement.State[0]);
    }

//    static GUIDescriptionBuilder getTagBaseDisplayBuilder(Tag tag) {
//        return getTagBaseDisplayBuilder(tag, false);
//    }
//
//    static GUIDescriptionBuilder getTagBaseDisplayBuilder(Tag tag, boolean hideDesc) {
//        int tagId = tag.getId();
//        String name = colorize(tag.getName());
//        String finalName = "§r" + name + ChatColor.WHITE + " (" + tagId + ")";
//        String desc = tag.getDescription() != null && !tag.getDescription().isEmpty() ? tag.getDescription() : "N/A";
//        TagRarity rarity = tag.getRarity();
//        TagType type = tag.getType();
//
//        if(hideDesc) {
//            desc = ChatColor.MAGIC + desc;
//        }
//        desc = ChatColor.GRAY + desc;
//
//        return new GUIDescriptionBuilder()
//                .raw(finalName)
//                .header("Tag Info")
//                .pair("Type", TagType.toColor(type) + type.name)
//                .pair("Rarity", rarity.color + rarity.name)
//                .pair("Description", GUIUtil.wrap(desc, 17, 30));
//    }

//    static GUIDescriptionBuilder getTagBaseWithExampleBuilder(Tag tag, String example) {
//        return getTagBaseDisplayBuilder(tag)
//                .blank()
//                .raw(example)
//                .blank();
//    }
//
//    static GUIDescriptionBuilder getFullTagDisplayBuilder(TagDTO tagData, String example, boolean owned) {
//        String date = tagData.getUnlocked() != null ? GUIUtil.ISOString(tagData.getUnlocked()) : "Not collected";
//        String plural = tagData.getOwnedCount() == 1 ? "player" : "players";
//
//        return getTagBaseDisplayBuilder(tagData.getTag(), !owned)
//                .blank()
//                .header("Tag Stats")
//                .pair("Collected", date)
//                .pair("Owned by", GUIUtil.getTagColour(tagData.getOwnedCount()) + "" + tagData.getOwnedCount() + " " + plural)
//                .blank()
//                .raw(example)
//                .blank();
//    }

    static String[] getDescription(String name, int position, List<String> options, String... after) {
        List<String> result = new ArrayList<>();
        result.add(name);

        for (int i = 0; i < options.size(); i++) {
            if (i == position) {
                result.add(ChatColor.GREEN + "➤ " + options.get(i));
            } else {
                result.add(ChatColor.RED + "  " + options.get(i));
            }
        }

        result.addAll(Arrays.asList(after));
        return result.toArray(new String[0]);
    }

    static ItemStack createPane(Dye colour) {
        return new ItemStack(Material.STAINED_GLASS_PANE, 1, colour.data);
    }

    static ItemStack createGlass(Dye colour) { return new ItemStack(Material.STAINED_GLASS, 1, colour.data); }

    static ItemStack createWool(Dye colour) {
        return new ItemStack(Material.WOOL, 1, colour.data);
    }

    static ChatColor boolColour(boolean val) {
        return val ? ChatColor.GREEN : ChatColor.RED;
    }

    static StaticGuiElement createPaneGuiElement(Dye colour, char slot) {
        return new StaticGuiElement(slot, createPane(colour), " ");
    }

    static String formatEnum(String string) {
        string = string.toLowerCase();
        string = string.replaceAll("_", " ");
        String[] words = string.split(" ");
        StringBuilder sb = new StringBuilder();

        for(String word : words) {
            sb.append(word.substring(0, 1).toUpperCase());
            sb.append(word.substring(1));
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    static String formatEnum(Enum<?> e) {
        return formatEnum(e.name());
    }

    static List<String> enumToList(Class<? extends Enum<?>> e) {

        List<String> names = new ArrayList<>();

        for(Enum<?> value : e.getEnumConstants()) {
            names.add(formatEnum(value.name()));
        }

        return names;
    }

    // I'm sure there is a much better word wrapping implementation..
    static String wrap(String desc, int start, int increment) {
        if (desc.length() > start) {
            StringBuilder sb = new StringBuilder(desc);
            for (int i = start; i < desc.length(); i += increment) {
                int spaceIndex = sb.lastIndexOf(" ", i);
                if (spaceIndex != -1) {
                    sb.replace(spaceIndex, spaceIndex + 1, "\n" + ChatColor.getLastColors(sb.substring(0, i)));
                }
            }
            desc = sb.toString();
        }
        return desc;
    }

    static String getStarColour(int stars) {
        if(stars < 3) {
            return ChatColor.GREEN + "";
        } else if (stars < 5) {
            return ChatColor.GOLD + "";
        } else if (stars < 7) {
            return ChatColor.RED + "";
        } else if (stars < 9) {
            return ChatColor.DARK_RED + "";
        } else {
            return ChatColor.DARK_PURPLE + "";
        }
    }

    static ChatColor getTagColour(long beaten) {
        ChatColor c;
        if(beaten <= 5) {
            c = ChatColor.DARK_PURPLE;
        } else if(beaten <= 15) {
            c = ChatColor.DARK_RED;
        } else if(beaten <= 30) {
            c = ChatColor.RED;
        } else if(beaten <= 75) {
            c = ChatColor.GOLD;
        } else if(beaten <= 150) {
            c = ChatColor.YELLOW;
        } else if(beaten <= 350) {
            c = ChatColor.GREEN;
        } else {
            c = ChatColor.DARK_GREEN;
        }
        return c;
    }

    static String getStars(int stars, boolean showNum) {
        String starLevel = getStarColour(stars) + "" + ChatColor.BOLD + "";
        if(showNum) {
            starLevel += String.format("%-2s", stars) + " ";
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < stars; i++) {
            sb.append("⚝");
        }

        for (int l = 10; l > stars; l--) {
            sb.append("⚝");
        }

        sb.insert(5, " ");
        int offset = stars > 5 ? stars + 1 : stars;
        sb.insert(offset, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "");

        return starLevel += sb.toString();
    }

    static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    static String ISOString(Date d) {
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    static void sendActionBar(Player p, String actionBar) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(actionBar), (byte) 2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    static String comma(int number) {
        return String.format("%,d", number);
    }
}