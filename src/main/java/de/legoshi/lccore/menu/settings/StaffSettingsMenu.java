package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.StaffPreferences;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.GuiToggleElement;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.function.Consumer;

public class StaffSettingsMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private DBManager db;
    private StaffPreferences prefs;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m12     m",
            "dmmmqmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Staff Settings", guiSetup);
        setColours(Dye.BLACK, Dye.YELLOW, Dye.BLACK);
        prefs = playerManager.getStaffPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        GUIDescriptionBuilder flyToggleDesc = new GUIDescriptionBuilder()
                .raw("Disable Fly On Join")
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        GUIDescriptionBuilder vanishToggleDesc = new GUIDescriptionBuilder()
                .raw("Vanish On Join")
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        GuiToggleElement mazeToggle = createToggleElement('1', prefs::setDisableFlyOnJoin, prefs.isDisableFlyOnJoin(), flyToggleDesc);
        GuiToggleElement wolfToggle = createToggleElement('2', prefs::setVanishOnJoin, prefs.isVanishOnJoin(), vanishToggleDesc);

        current.addElements(mazeToggle.build(), wolfToggle.build(), returnToParent);
    }


    GuiToggleElement createToggleElement(char key, Consumer<Boolean> toggleAction, boolean initialState, GUIDescriptionBuilder desc) {
        return new GuiToggleElement(key, desc, initialState).setOnEnable(() -> {
            toggleAction.accept(true);
            db.update(prefs);
            return true;
        }).setOnDisable(() -> {
            toggleAction.accept(false);
            db.update(prefs);
            return true;
        });
    }
}
