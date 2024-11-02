package de.legoshi.lccore.menu.cosmetics;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.HeadUtil;
import de.legoshi.lccore.util.ItemBuilder;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSkull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class CosmeticsMenu extends GUIScrollablePane {

    @Inject Injector injector;

    String[] guiSetup = {
            "-a-b-c-d-"
    };

    public void openGui(Player player) {
        super.openGui(player, null);
        String title = "Cosmetics";

        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);

        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {

        GUIDescriptionBuilder helmetDescription = new GUIDescriptionBuilder().raw(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Helmet Cosmetics");
        GUIDescriptionBuilder chestplateDescription = new GUIDescriptionBuilder().raw(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Chestplate Cosmetics");
        GUIDescriptionBuilder leggingsDescription = new GUIDescriptionBuilder().raw(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Leggings Cosmetics");
        GUIDescriptionBuilder bootsDescription = new GUIDescriptionBuilder().raw(ChatColor.BLUE + String.valueOf(ChatColor.BOLD) + "Boots Cosmetics");


        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) new ItemStack(Material.LEATHER_BOOTS).getItemMeta();
        leatherArmorMeta.setColor(Color.BLUE);

        ItemStack helmet = HeadUtil.playerHead(this.holder.getName());
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        chestplate.setItemMeta(leatherArmorMeta);
        leggings.setItemMeta(leatherArmorMeta);
        boots.setItemMeta(leatherArmorMeta);

        StaticGuiElement helmetElement = new StaticGuiElement('a', helmet, click -> {
            injector.getInstance(CosmeticsListMenu.class).openGui(this.holder, CosmeticType.HELMET, this.current);
            return true;
        }, helmetDescription.build());

        StaticGuiElement chestplateElement = new StaticGuiElement('b', chestplate, click -> {
            injector.getInstance(CosmeticsListMenu.class).openGui(this.holder, CosmeticType.CHESTPLATE, this.current);
            return true;
        }, chestplateDescription.build());

        StaticGuiElement leggingsElement = new StaticGuiElement('c', leggings, click -> {
            injector.getInstance(CosmeticsListMenu.class).openGui(this.holder, CosmeticType.LEGGINGS, this.current);
            return true;
        }, leggingsDescription.build());

        StaticGuiElement bootsElement = new StaticGuiElement('d', boots, click -> {
            injector.getInstance(CosmeticsListMenu.class).openGui(this.holder, CosmeticType.BOOTS, this.current);
            return true;
        }, bootsDescription.build());

        this.current.addElements(helmetElement, chestplateElement, leggingsElement, bootsElement);

    }

    @Override
    protected void getPage() {

    }
}
