package de.legoshi.lccore.cosmetics;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Cosmetic {

    void startAnimation(Player player);
    void stopAnimation(Player player);
    String getName();
    String getDisplayName();
    String getUncoloredName();
    CosmeticType getType();
    ItemStack getDisplay();
    static Set<String> cosmeticList = new HashSet<>();

    static void registerCosmetic(String cosmetic) {
        cosmeticList.add(cosmetic);
    }
}
