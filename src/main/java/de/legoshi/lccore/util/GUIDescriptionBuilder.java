package de.legoshi.lccore.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;

public class GUIDescriptionBuilder {

    //private char
    private String header;
    private ChatColor headerColour;
    private ChatColor prefixColour;
    private ChatColor keyColour;
    private ChatColor valueDefault;
    private ChatColor guiActionDefault;
    private String prefix;
    private String key;
    private ArrayList<String> lines;

    public GUIDescriptionBuilder() {
        lines = new ArrayList<>();
        headerColour = ChatColor.DARK_GRAY;
        prefixColour = ChatColor.GRAY;
        keyColour = ChatColor.YELLOW;
        valueDefault = ChatColor.GRAY;
        guiActionDefault = ChatColor.DARK_GRAY;
        //header = "{colour}" + "◯§n§l╭{0}╮§r" + "{colour}" + "◯";
        header = "{colour}§m-----§r{colour}§l{0}§r{colour}§m-----";
        prefix = "{colour}»";
        key = "{colour}{0}:";
    }

    public GUIDescriptionBuilder(GUIDescriptionBuilder other) {
        this.header = other.header;
        this.headerColour = other.headerColour;
        this.prefixColour = other.prefixColour;
        this.keyColour = other.keyColour;
        this.valueDefault = other.valueDefault;
        this.guiActionDefault = other.guiActionDefault;
        this.prefix = other.prefix;
        this.key = other.key;
        this.lines = new ArrayList<>(other.lines);
    }

    private void addLine(String text) {
        lines.add(ChatColor.RESET + text);
    }

    public GUIDescriptionBuilder header(String name) {
        String updated = CommonUtil.format(header, "{colour}", headerColour);
        addLine(CommonUtil.format(updated, "{0}", name));
        return this;
    }

    public GUIDescriptionBuilder pair(String key, String value) {
        String updated = CommonUtil.format(prefix, "{colour}", prefixColour);
        String updatedK = CommonUtil.format(this.key, "{colour}", keyColour);
        String updatedK2 = CommonUtil.format(updatedK, "{0}", key);
        addLine(updated + " " + updatedK2 + ChatColor.RESET + " " + valueDefault + value);
        return this;
    }

    public GUIDescriptionBuilder pairCustom(String key, String separator, String value) {
        addLine(key + separator + value);
        return this;
    }

    public GUIDescriptionBuilder raw(String string) {
        addLine(string);
        return this;
    }

    public GUIDescriptionBuilder wrap(String string) {
        addLine(GUIUtil.wrap(string, 50, 50));
        return this;
    }

    public GUIDescriptionBuilder action(GUIAction action, String desc) {
        addLine(guiActionDefault + action.display + " - " + desc);
        return this;
    }

    public GUIDescriptionBuilder blank() {
        addLine("");
        return this;
    }

    public GUIDescriptionBuilder blank(int n) {
        for (int i = 0; i < n; i++) {
            addLine("");
        }
        return this;
    }

    public GUIDescriptionBuilder removeAt(int idx) {
        if(idx >= 0 && idx < lines.size()) {
            lines.remove(idx);
        }
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            sb.append(line).append("\n");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }
}
