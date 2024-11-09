package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerPreferences;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.GUIUtil;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class PracticeItemHotbarPlacementMenu extends GUIPane {
    @Inject
    private PlayerManager playerManager;
    @Inject private DBManager db;
    @Inject private Injector injector;
    @Inject private ChatManager chatManager;
    private PlayerPreferences prefs;

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "012345678",
            "dmmmqmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Practice Item Hotbar Placement", guiSetup);
        setColours(Dye.WHITE, Dye.LIME, Dye.BLACK);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        loadData();
        current.addElements(returnToParent);
    }

    private void loadData() {
        this.prefs = playerManager.getPlayerPrefs(holder);

        for(int i = 0; i < 9; i++) {
            char itemPos = String.valueOf(i).charAt(0);
            current.removeElement(itemPos);
            boolean isEquipped = prefs.getPracticeHotbarPosition() != null && prefs.getPracticeHotbarPosition() == i;
            ItemStack item = isEquipped ? new ItemStack(Material.INK_SACK, 1, (short)10) : new ItemStack(Material.INK_SACK, 1, (short)8);

            int finalI = i;
            StaticGuiElement slotElement = new StaticGuiElement(itemPos, item, click -> {
                if(click.getType().isLeftClick() && !isEquipped) {
                    prefs.setPracticeHotbarPosition(finalI);
                    db.update(prefs);
                    click.getGui().playClickSound();
                } else if(click.getType().isRightClick() && isEquipped) {
                    prefs.setPracticeHotbarPosition(null);
                    db.update(prefs);
                    click.getGui().playClickSound();
                }
                loadData();
                current.draw();
                return true;
            }, new GUIDescriptionBuilder()
                    .raw("Slot " + i)
                    .pair("Selected", GUIUtil.trueFalseDisplay(isEquipped))
                    .action(GUIAction.LEFT_CLICK, "Select")
                    .action(GUIAction.RIGHT_CLICK, "Deselect")
                    .build());

            current.addElement(slotElement);
        }
    }
}
