package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

public class CosmeticSettingsMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private DBManager dbManager;
    private PlayerPreferences playerPreferences;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m  bwa  m",
            "dmmmqmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Cosmetic Settings", guiSetup);
        setColours(Dye.WHITE, Dye.GRAY, Dye.BLACK);
        playerPreferences = playerManager.getPlayerPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        GuiStateElement toggleMazeVisibility = new GuiStateElement('b',
                new GuiStateElement.State(
                        change -> {
                            playerPreferences.setMazeDisplay(true);
                            dbManager.update(playerPreferences);
                        },
                        "enabled",
                        new ItemStack(Material.INK_SACK, 1, (short)10),
                        new GUIDescriptionBuilder().raw("Tab Maze Display")
                                .pair("Enabled", "true")
                                .build()
                ),
                new GuiStateElement.State(
                        change -> {
                            playerPreferences.setMazeDisplay(false);
                            dbManager.update(playerPreferences);
                        },
                        "disabled",
                        new ItemStack(Material.INK_SACK, 1, (short)8),
                        new GUIDescriptionBuilder().raw("Tab Maze Display")
                                .pair("Enabled", "false")
                                .build()
                )
        );

        toggleMazeVisibility.setState(playerPreferences.isMazeDisplay() ? "enabled" : "disabled");


        GuiStateElement toggleWolfVisibility = new GuiStateElement('w',
                new GuiStateElement.State(
                        change -> {
                            playerPreferences.setWolfDisplay(true);
                            dbManager.update(playerPreferences);
                        },
                        "enabled",
                        new ItemStack(Material.INK_SACK, 1, (short)10),
                        new GUIDescriptionBuilder().raw("Chat Wolf Display")
                                .pair("Enabled", "true")
                                .build()
                ),
                new GuiStateElement.State(
                        change -> {
                            playerPreferences.setWolfDisplay(false);
                            dbManager.update(playerPreferences);
                        },
                        "disabled",
                        new ItemStack(Material.INK_SACK, 1, (short)8),
                        new GUIDescriptionBuilder().raw("Chat Wolf Display")
                                .pair("Enabled", "false")
                                .build()
                )
        );

        toggleWolfVisibility.setState(playerPreferences.isWolfDisplay() ? "enabled" : "disabled");


        GuiStateElement toggleBonusVisibility = new GuiStateElement('a',
                new GuiStateElement.State(
                        change -> {
                            playerPreferences.setBonusDisplay(true);
                            dbManager.update(playerPreferences);
                        },
                        "enabled",
                        new ItemStack(Material.INK_SACK, 1, (short)10),
                        new GUIDescriptionBuilder().raw("Chat Bonus Display")
                                .pair("Enabled", "true")
                                .build()
                ),
                new GuiStateElement.State(
                        change -> {
                            playerPreferences.setBonusDisplay(false);
                            dbManager.update(playerPreferences);
                        },
                        "disabled",
                        new ItemStack(Material.INK_SACK, 1, (short)8),
                        new GUIDescriptionBuilder().raw("Chat Bonus Display")
                                .pair("Enabled", "false")
                                .build()
                )
        );

        toggleBonusVisibility.setState(playerPreferences.isBonusDisplay() ? "enabled" : "disabled");

        current.addElements(toggleMazeVisibility, toggleBonusVisibility, toggleWolfVisibility, returnToParent);
    }
}
