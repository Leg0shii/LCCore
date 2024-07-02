package de.legoshi.lccore.menu.shop;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class ShopMenu extends GUIPane {

    @Inject private Injector injector;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m cshep m",
            "dmmmmmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Shop", guiSetup);
        setColours(Dye.WHITE, Dye.GRAY, Dye.BLACK);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement commandsShopMenu = new StaticGuiElement('c', new ItemStack(Material.COMMAND_MINECART), click -> {
            injector.getInstance(CommandShopMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.YELLOW + "" + ChatColor.BOLD + "Commands")
                .build());

        StaticGuiElement starShopMenu = new StaticGuiElement('s', new ItemStack(Material.NETHER_STAR), click -> {
            injector.getInstance(StarShopMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.AQUA + "" + ChatColor.BOLD + "Stars")
                .build());

        StaticGuiElement chatColourShopMenu = new StaticGuiElement('h', new ItemStack(Material.WOOL, 1, Dye.RED.data), click -> {
            injector.getInstance(ChatColorShopMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.RED + "" + ChatColor.BOLD + "Chat Colors")
                .build());

        StaticGuiElement chatEmojiShopMenu = new StaticGuiElement('e', new ItemStack(Material.DOUBLE_PLANT), click -> {
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Emojis")
                .coloured("UNDER CONSTRUCTION", ChatColor.RED)
                .build());

        StaticGuiElement practiceItemShopMenu = new StaticGuiElement('p', new ItemStack(Material.SLIME_BALL), click -> {
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.GREEN + "" + ChatColor.BOLD + "Practice Items")
                .coloured("UNDER CONSTRUCTION", ChatColor.RED)
                .build());

        current.addElements(commandsShopMenu, starShopMenu, chatColourShopMenu, chatEmojiShopMenu, practiceItemShopMenu);
    }
}
