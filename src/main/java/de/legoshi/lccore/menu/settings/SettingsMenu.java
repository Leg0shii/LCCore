package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
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

public class SettingsMenu extends GUIPane {

    @Inject private Injector injector;
    @Inject private PlayerManager playerManager;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m g c p m",
            "dmmmmmmmd",
    };

    private final String[] guiStaffSetup = {
            "dmmmmmmmd",
            "mg c p sm",
            "dmmmmmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);

        String[] setup = playerManager.isStaff(player) ? guiStaffSetup : guiSetup;
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Settings", setup);
        setColours(Dye.WHITE, Dye.GRAY, Dye.BLACK);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement generalSettingsMenu = new StaticGuiElement('g', new ItemStack(Material.COMPASS), click -> {
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.AQUA + "" + ChatColor.BOLD + "General Settings")
                .coloured("UNDER CONSTRUCTION", ChatColor.RED)
                .build());

        StaticGuiElement cosmeticSettingsMenu = new StaticGuiElement('c', new ItemStack(Material.INK_SACK, 1, (short)9), click -> {
            injector.getInstance(CosmeticSettingsMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Cosmetic Settings").build());

        StaticGuiElement practiceSettingsMenu = new StaticGuiElement('p', new ItemStack(Material.SLIME_BALL), click -> {
            injector.getInstance(PracticeSettingsMenu.class).openGui(holder, current);

            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.GREEN + "" + ChatColor.BOLD + "Practice Settings")
                .build());

        StaticGuiElement staffSettingsMenu = new StaticGuiElement('s', new ItemStack(Material.REDSTONE), click -> {
            injector.getInstance(StaffSettingsMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.YELLOW + "" + ChatColor.BOLD + "Staff Settings")
                .build());

        current.addElements(generalSettingsMenu, cosmeticSettingsMenu, practiceSettingsMenu, staffSettingsMenu);
    }
}
