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
        if (Linkcraft.getInstance() == null)
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
            String chat = "";
            if (ColorHelper.getChatColor(p) != null) {
                chat = chat + "&" + ColorHelper.getChatColor(p).getCode();
            }
            for (ColorHelper.ChatFormat formats : ColorHelper.getChatFormats(p)) chat = chat + "&" + formats.getCode();
            return chat;
        }
        if (id.equalsIgnoreCase("secondprefix")) return SecondPrefix.getPrefix(p);
        return null;
    }
}