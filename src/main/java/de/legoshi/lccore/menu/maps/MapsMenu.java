package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.util.CommandConfirmationMenu;
import de.legoshi.lccore.util.Dye;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class MapsMenu extends GUIPane {

    @Inject private Injector injector;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "s v   w s",
            "s   z   s",
            "s x   y s",
            "ddmmcmmdd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Maps", guiSetup);
        setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement bonus = new StaticGuiElement('v', new ItemStack(Material.GOLD_INGOT), click -> {
            injector.getInstance(BonusConfirmationMenu.class).openGui(holder, current);
            return true;
        }, "§e§lBonus Rankups");

        StaticGuiElement side = new StaticGuiElement('w', new ItemStack(Material.IRON_INGOT), click -> {
            injector.getInstance(MapsHolder.class).openGui(holder, current);
            return true;
        }, "§f§lSide Courses");

        StaticGuiElement main = new StaticGuiElement('z', new ItemStack(Material.DIAMOND), click -> {
            injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp rankup", "§aWarp to rankup lobby!");
            return true;
        }, "§b§lMain Rankups");

        StaticGuiElement segmented = new StaticGuiElement('x', new ItemStack(Material.EMERALD), click -> {
            injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp challenge", "§aWarp to challenge lobby!");
            return true;
        }, "§a§lSegmented Challenges");

        StaticGuiElement maze = new StaticGuiElement('y', new ItemStack(Material.REDSTONE), click -> {
            injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp maze", "§aWarp to maze lobby!");
            return true;
        }, "§c§lMaze Rankups");

        current.addElements(bonus, side, main, segmented, maze);
    }
}
