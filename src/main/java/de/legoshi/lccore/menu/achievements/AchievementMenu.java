package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class AchievementMenu extends GUIPane {

    @Inject private Injector injector;
    private Player player;
    private Player target;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "s-------s",
            "s-x-y-z-s",
            "s-------s",
            "ddmmcmmdd",
    };

    public void openGui(Player player, Player target, InventoryGui parent) {
        super.openGui(player, parent);

        this.player = player;
        this.target = target;

        String title = target.getName() + "'s Achievements";

        if (player.getName().equals(target.getName())) {
            title = "Achievements";
        }

        this.current = new InventoryGui(Linkcraft.getPlugin(), target, title, guiSetup);
        setColours(Dye.RED, Dye.ORANGE, Dye.YELLOW);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);

    }

    @Override
    protected void registerGuiElements() {

        GUIDescriptionBuilder normalBuilder = new GUIDescriptionBuilder()
                .raw("&e&lNormal Achievements")
                .blank();

        ItemStack normalAchItem = new ItemStack(Material.DOUBLE_PLANT);

        StaticGuiElement normalAchElement = new StaticGuiElement('x', normalAchItem, click -> {
            injector.getInstance(NormalAchievementMenu.class).openGui(player, target, null);
            return true;
        }, normalBuilder.build());

        GUIDescriptionBuilder grindBuilder = new GUIDescriptionBuilder()
                .raw("&b&lGrind Achievements")
                .blank();

        ItemStack grindAchItem = new ItemStack(Material.DIAMOND_BLOCK);

        StaticGuiElement grindAchElement = new StaticGuiElement('y', grindAchItem, click -> {
            injector.getInstance(NormalAchievementMenu.class).openGui(player, target, null);
            return true;
        }, grindBuilder.build());

        GUIDescriptionBuilder secretBuilder = new GUIDescriptionBuilder()
                .raw("&5&lSecret Achievements")
                .blank();

        ItemStack secretAchItem = new ItemStack(Material.MAP);

        StaticGuiElement secretAchElement = new StaticGuiElement('z', secretAchItem, click -> {
            injector.getInstance(NormalAchievementMenu.class).openGui(player, target, null);
            return true;
        }, secretBuilder.build());



        current.addElements(normalAchElement, grindAchElement, secretAchElement);
    }

}
