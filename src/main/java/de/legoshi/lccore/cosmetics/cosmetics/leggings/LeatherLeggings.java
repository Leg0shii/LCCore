package de.legoshi.lccore.cosmetics.cosmetics.leggings;

import de.legoshi.lccore.cosmetics.ColorCosmetic;
import de.legoshi.lccore.cosmetics.CosmeticType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherLeggings implements ColorCosmetic {

    public Color color;

    @Override
    public void startAnimation(Player player) {
        equipArmor(player, null);
    }

    public void equipArmor(Player player, Color color) {

        ItemStack itemStack = getDisplay();

        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(color);
        itemStack.setItemMeta(leatherArmorMeta);

        player.getInventory().setLeggings(itemStack);
    }


    public void stopAnimation(Player player) {

    }


    public String getName() {
        return "LeatherLeggings";
    }


    public String getDisplayName() {
        return "Leather Leggings";
    }


    public String getUncoloredName() {
        return "Leather Leggings";
    }


    public CosmeticType getType() {
        return CosmeticType.LEGGINGS;
    }

    public ItemStack getDisplay() {

        return new ItemStack(Material.LEATHER_LEGGINGS);
    }
}
