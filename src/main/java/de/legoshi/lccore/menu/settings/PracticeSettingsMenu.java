package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.menu.GuiToggleElement;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.function.Consumer;

public class PracticeSettingsMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private DBManager db;
    @Inject private Injector injector;
    @Inject private ChatManager chatManager;
    private PlayerPreferences prefs;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m  123  m",
            "dmmmqmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Practice Settings", guiSetup);
        setColours(Dye.WHITE, Dye.LIME, Dye.BLACK);
        prefs = playerManager.getPlayerPrefs(holder);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        GUIDescriptionBuilder oldPracticeDesc = new GUIDescriptionBuilder()
                .raw("Old Practice")
                .wrap(ChatColor.BLUE + "This setting may help reduce practice glitchyness for players with high ping or ping spikes. Please note that if this setting is enabled, we reserve the right to deny any tp back request if practice coordinates change upon unpracticing")
                .action(GUIAction.LEFT_CLICK, "Enable/Disable");

        GuiToggleElement oldPracticeToggle = createToggleElement('1', prefs::setOldPractice, prefs.isOldPractice(), oldPracticeDesc);


        StaticGuiElement practiceItemSettings = new StaticGuiElement('2', new ItemStack(Material.SLIME_BALL), click -> {
            //injector.getInstance(PracticeItemHotbarPlacementMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.GREEN + "" + ChatColor.BOLD + "Practice Items")
                .coloured("UNDER CONSTRUCTION", ChatColor.RED)
                .build());

        StaticGuiElement practiceHotbarSettings = new StaticGuiElement('3', new ItemStack(Material.REDSTONE_COMPARATOR), click -> {
            injector.getInstance(PracticeItemHotbarPlacementMenu.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder()
                .raw(ChatColor.RED + "" + ChatColor.BOLD + "Practice Hotbar Placement")
                .build());



        current.addElements(practiceHotbarSettings, oldPracticeToggle.build(), practiceItemSettings, returnToParent);
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
