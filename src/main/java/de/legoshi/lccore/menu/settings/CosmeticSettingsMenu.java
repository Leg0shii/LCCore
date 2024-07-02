package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.ChatManager;
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

public class CosmeticSettingsMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private DBManager dbManager;
    @Inject private Injector injector;
    @Inject private ChatManager chatManager;
    private PlayerPreferences playerPreferences;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "mshfnbwam",
            "dmmmqmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Cosmetic Settings", guiSetup);
        setColours(Dye.WHITE, Dye.PINK, Dye.BLACK);
        playerPreferences = playerManager.getPlayerPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement starSettingsMenu = new StaticGuiElement('s', new ItemStack(Material.NETHER_STAR), click -> {
            injector.getInstance(StarSettingsMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.AQUA + "" + ChatColor.BOLD + "Stars")
                .build());

        StaticGuiElement chatColorSettingsMenu = new StaticGuiElement('h', new ItemStack(Material.WOOL, 1, Dye.RED.data), click -> {
            injector.getInstance(ChatColorSettingsMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.RED + "" + ChatColor.BOLD + "Chat Colors")
                .build());

        StaticGuiElement chatFormatSettingsMenu = new StaticGuiElement('f', new ItemStack(Material.SIGN), click -> {
            injector.getInstance(ChatFormatMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.GOLD + "" + ChatColor.BOLD + "Chat Formats")
                .build());

        StaticGuiElement nameFormatSettingsMenu = new StaticGuiElement('n', new ItemStack(Material.NAME_TAG), click -> {
            injector.getInstance(NameFormatMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Name Formats")
                .build());

        StaticGuiElement displayTogglesMenu = new StaticGuiElement('b', new ItemStack(Material.PISTON_BASE), click -> {
            injector.getInstance(DisplayToggleMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.YELLOW + "" + ChatColor.BOLD + "Display Toggles")
                .build());

        current.addElements(starSettingsMenu, chatColorSettingsMenu, chatFormatSettingsMenu, nameFormatSettingsMenu, displayTogglesMenu, returnToParent);
    }
}
