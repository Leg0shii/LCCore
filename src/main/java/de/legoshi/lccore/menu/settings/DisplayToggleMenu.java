package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.GuiToggleElement;
import de.legoshi.lccore.menu.GuiToggleUpdateGroup;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.function.Consumer;
import java.util.function.Function;

public class DisplayToggleMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;
    @Inject private DBManager db;
    private GuiToggleUpdateGroup updateGroup = new GuiToggleUpdateGroup();
    private PlayerPreferences prefs;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m  123  m",
            "dmmmqmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Display Toggles", guiSetup);
        setColours(Dye.WHITE, Dye.PINK, Dye.BLACK);
        prefs = playerManager.getPlayerPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {

        GUIDescriptionBuilder mazeDesc = new GUIDescriptionBuilder()
                .raw("Tab Maze Display")
                .blank()
                .raw(chatManager.tabDisplay(holder))
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");


        GUIDescriptionBuilder wolfDesc = new GUIDescriptionBuilder()
                .raw("Chat Wolf Display")
                .blank()
                .raw(chatManager.globalChatExample(holder))
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        GUIDescriptionBuilder bonusDesc = new GUIDescriptionBuilder()
            .raw("Chat Bonus Display")
            .blank()
            .raw(chatManager.globalChatExample(holder))
            .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        GuiToggleElement mazeToggle = createToggleElement('1', prefs::setMazeDisplay, prefs.isMazeDisplay(), mazeDesc, chatManager::tabDisplay, false);
        GuiToggleElement wolfToggle = createToggleElement('2', prefs::setWolfDisplay, prefs.isWolfDisplay(), wolfDesc, chatManager::globalChatExample, true);
        GuiToggleElement bonusToggle = createToggleElement('3', prefs::setBonusDisplay, prefs.isBonusDisplay(), bonusDesc, chatManager::globalChatExample, true);
        updateGroup.add(wolfToggle, bonusToggle);
        current.addElements(returnToParent, mazeToggle.build(), wolfToggle.build(), bonusToggle.build());
    }

    GuiToggleElement createToggleElement(char key, Consumer<Boolean> toggleAction, boolean initialState, GUIDescriptionBuilder desc, Function<Player, String> descUpdater, boolean inGroup) {
        return new GuiToggleElement(key, desc, initialState).setInGroup(inGroup).setOnEnable(() -> {
            toggleAction.accept(true);
            db.update(prefs);
            updateGroup.update();
            return true;
        }).setOnDisable(() -> {
            toggleAction.accept(false);
            db.update(prefs);
            updateGroup.update();
            return true;
        }).setOnDescriptionUpdate(updatedDesc -> updatedDesc.replaceAt(descUpdater.apply(holder), 3));
    }
}
