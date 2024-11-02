package de.legoshi.lccore.cosmetics.cosmetics.boots;

import de.legoshi.lccore.cosmetics.ColorCosmetic;
import de.legoshi.lccore.cosmetics.CosmeticType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeatherBoots implements ColorCosmetic {

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

        player.getInventory().setBoots(itemStack);
    }


    public void stopAnimation(Player player) {

    }


    public String getName() {
        return "LeatherBoots";
    }


    public String getDisplayName() {
        return "Leather Boots";
    }


    public String getUncoloredName() {
        return "Leather Boots";
    }


    public CosmeticType getType() {
        return CosmeticType.BOOTS;
    }

    public ItemStack getDisplay() {

        return new ItemStack(Material.LEATHER_BOOTS);
    }
}
