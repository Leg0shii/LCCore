package de.legoshi.lccore.menu.cosmetics;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.manager.CosmeticManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.util.Dye;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import team.unnamed.inject.Inject;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

public class CosmeticsColorMenu extends GUIScrollablePane {

    @Inject
    CosmeticManager cosmeticManager;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "-w-x-y-z-",
    };

    private CosmeticType cosmeticType;

    public void openGui(Player player, CosmeticType cosmeticType) {

        super.openGui(player, null);

        this.cosmeticType = cosmeticType;
        String title = "Change Cosmetic Color";
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);

        setColours(Dye.BLACK, Dye.GRAY, Dye.WHITE);
        fullCloseOnEsc();
        registerGuiElements();

        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        getPage();
    }

    @Override
    protected void getPage() {

        createDyeItems();

        createSelectableArmor('w', Material.LEATHER_BOOTS, CosmeticType.BOOTS);
        createSelectableArmor('x', Material.LEATHER_LEGGINGS, CosmeticType.LEGGINGS);
        createSelectableArmor('y', Material.LEATHER_CHESTPLATE, CosmeticType.CHESTPLATE);
        createSelectableArmor('z', Material.LEATHER_HELMET, CosmeticType.HELMET);

    }

    public void createSelectableArmor(char chr, Material material, CosmeticType cosmeticType) {

        this.current.removeElement(chr);

        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (this.cosmeticType == cosmeticType) {
            meta.setColor(Color.LIME);
            meta.addEnchant(Enchantment.LUCK, 10, false); }
        else { meta.setColor(Color.RED); }
        item.setItemMeta(meta);

        StaticGuiElement element = new StaticGuiElement(chr, item);

        element.setAction(click -> {
            if (this.cosmeticType != cosmeticType) {
                openGui(this.holder, cosmeticType);
            }
            return true;
        });

        this.current.addElement(element);
    }

    private void clearCurrentElements() {
        for (char chr : new char[]{'w', 'x', 'y', 'z'}) {
            this.current.removeElement(chr);
        }
    }

    public void createDyeItems() {

        GuiElementGroup group = new GuiElementGroup('g');

        for (Dye dye : Dye.values()) {

            ItemStack dyeItem = new ItemStack(Material.INK_SACK, 1, (short) 8);
            ItemMeta meta = dyeItem.getItemMeta();
            meta.setDisplayName(cosmeticManager.getChatColorFromDye(dye) + cosmeticManager.getColorStringFromDye(dye));
            meta.setLore(Arrays.asList("", ChatColor.RED + "You haven't unlocked this Color!"));


            GuiElement.Action action = null;

            if (cosmeticManager.hasCosmetic(this.holder, cosmeticManager.getColorStringFromDye(dye))) {
                dyeItem.setDurability((short) dye.ordinal());
                action = click -> {
                    cosmeticManager.setColor(holder, cosmeticType, cosmeticManager.getColorStringFromDye(dye));
                    return true;
                };

                meta.setLore(Arrays.asList("", cosmeticManager.getChatColorFromDye(dye) + "Click to select this Color!"));
                
            }

            dyeItem.setItemMeta(meta);

            StaticGuiElement element = new StaticGuiElement('g', dyeItem);
            element.setAction(action);

            group.addElement(element);
        }

        this.current.addElement(group);
    }

}
