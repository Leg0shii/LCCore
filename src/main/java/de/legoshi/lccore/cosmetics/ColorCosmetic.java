package de.legoshi.lccore.cosmetics;

import org.bukkit.Color;
import org.bukkit.entity.Player;

public interface ColorCosmetic extends Cosmetic {
    void equipArmor(Player player, Color color);
}
