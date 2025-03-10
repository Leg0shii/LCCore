package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.util.CommandConfirmationMenu;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.MapType;
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
            "sy     ws",
            "s v z u s",
            "sx     as",
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
            if(click.getType().isLeftClick()) {
                injector.getInstance(BonusMapsConfirmationMenu.class).openGui(holder, current);
            } else if(click.getType().isRightClick()) {
                injector.getInstance(BonusConfirmationMenu.class).openGui(holder, current);
            }

            return true;
        }, new GUIDescriptionBuilder().raw("§e§lBonus Rankups")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .action(GUIAction.RIGHT_CLICK, "Warp To Lobby")
                .build());

        StaticGuiElement side = new StaticGuiElement('w', new ItemStack(Material.IRON_INGOT), click -> {
            if(click.getType().isLeftClick()) {
                injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.SIDE);
            } else if(click.getType().isRightClick()) {
                injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp parkour", "§aWarp to parkour lobby!");
            }
            return true;
        }, new GUIDescriptionBuilder().raw("§f§lSide Courses")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .action(GUIAction.RIGHT_CLICK, "Warp To Lobby")
                .build());

        StaticGuiElement main = new StaticGuiElement('z', new ItemStack(Material.DIAMOND), click -> {
            if(click.getType().isLeftClick()) {
                injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.RANKUP);
            } else if(click.getType().isRightClick()) {
                injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp rankup", "§aWarp to rankup lobby!");
            }
            return true;
        }, new GUIDescriptionBuilder().raw("§b§lMain Rankups")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .action(GUIAction.RIGHT_CLICK, "Warp To Lobby")
                .build());

        StaticGuiElement segmented = new StaticGuiElement('x', new ItemStack(Material.EMERALD), click -> {
            if (click.getType().isLeftClick()) {
                injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.CHALLENGE);
            } else if (click.getType().isRightClick()) {
                injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp challenge", "§aWarp to challenge lobby!");
            }
            return true;
        }, new GUIDescriptionBuilder().raw("§a§lSegmented Challenges")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .action(GUIAction.RIGHT_CLICK, "Warp To Lobby")
                .build());

        StaticGuiElement maze = new StaticGuiElement('y', new ItemStack(Material.REDSTONE), click -> {
            if (click.getType().isLeftClick()) {
                injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.MAZE);
            } else if (click.getType().isRightClick()) {
                injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp maze", "§aWarp to maze lobby!");
            }
            return true;
        }, new GUIDescriptionBuilder().raw("§c§lMaze Rankups")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .action(GUIAction.RIGHT_CLICK, "Warp To Lobby")
                .build());

        StaticGuiElement wolf = new StaticGuiElement('u', new ItemStack(Material.BONE), click -> {
            if (click.getType().isLeftClick()) {
                injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.WOLF);
            } else if (click.getType().isRightClick()) {
                injector.getInstance(CommandConfirmationMenu.class).openGui(holder, current, "warp wolfrankup", "§aWarp to wolf lobby!");
            }
            return true;
        }, new GUIDescriptionBuilder().raw("§7§lWolf Rankups")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .action(GUIAction.RIGHT_CLICK, "Warp To Lobby")
                .build());

        StaticGuiElement archived = new StaticGuiElement('a', new ItemStack(Material.BOOK), click -> {
            injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.LEGACY);
            return true;
        }, new GUIDescriptionBuilder().raw("§8§lLegacy Courses")
                .action(GUIAction.LEFT_CLICK, "Open Menu")
                .build());


        current.addElements(bonus, side, main, segmented, maze, wolf, archived);
    }
}
