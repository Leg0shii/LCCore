package de.legoshi.lccore.papi;

import com.darkender.SecondPrefix.SecondPrefix;
import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ColorHelper;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceHolderAPI extends PlaceholderExpansion {
    public boolean canRegister() {
        return (Bukkit.getPluginManager().getPlugin("LCEssentials") != null);
    }

    public boolean register() {
        if (!canRegister())
            return false;
        if (Linkcraft.getPlugin() == null)
            return false;
        return super.register();
    }

    public String getRequiredPlugin() {
        return "Linkcraft";
    }

    public String getAuthor() {
        return "Leg0shi_";
    }

    public String getIdentifier() {
        return "lcessentials";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player p, String id) {
        if (id.equalsIgnoreCase("theheheehthej")) {
            StringBuilder chat = new StringBuilder();
            if (ColorHelper.getChatColor(p) != null) {
                chat.append("&").append(ColorHelper.getChatColor(p).getCode());
            }
            for (ColorHelper.ChatFormat formats : ColorHelper.getChatFormats(p)) chat.append("&").append(formats.getCode());
            return chat.toString();
        }
        if (id.equalsIgnoreCase("secondprefix")) return SecondPrefix.getPrefix(p);
        return null;
    }
}