package de.legoshi.lccore.cosmetics.cosmetics.helmet;

import de.legoshi.lccore.cosmetics.ColorCosmetic;
import de.legoshi.lccore.cosmetics.CosmeticType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherHelmet implements ColorCosmetic {

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

        player.getInventory().setHelmet(itemStack);
    }


    public void stopAnimation(Player player) {

    }


    public String getName() {
        return "LeatherHelmet";
    }


    public String getDisplayName() {
        return "Leather Helmet";
    }


    public String getUncoloredName() {
        return "Leather Helmet";
    }


    public CosmeticType getType() {
        return CosmeticType.HELMET;
    }

    public ItemStack getDisplay() {

        return new ItemStack(Material.LEATHER_HELMET);
    }
}
