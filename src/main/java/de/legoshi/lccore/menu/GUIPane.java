package de.legoshi.lccore.menu;

import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIUtil;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUIPane {

    protected InventoryGui parent;
    protected InventoryGui current;

    protected Player holder;
    protected StaticGuiElement returnToParent;
    protected StaticGuiElement outside;
    protected StaticGuiElement between;
    protected StaticGuiElement middle;
    protected StaticGuiElement sides;
    protected Runnable onReturn = () -> {};

    public void openGui(Player player, InventoryGui parent) {
        this.holder = player;
        this.parent = parent;
        registerPageElements();
    }

    protected abstract void registerGuiElements();

    private void registerPageElements() {
        // Defaults
        this.outside = GUIUtil.createPaneGuiElement(Dye.PURPLE, 'd');
        this.between = GUIUtil.createPaneGuiElement(Dye.MAGENTA, 'm');
        this.middle = GUIUtil.createPaneGuiElement(Dye.PINK, 'c');
        this.sides = GUIUtil.createPaneGuiElement(Dye.BLACK, 's');
        this.returnToParent = new StaticGuiElement('q', new ItemStack(Material.IRON_INGOT), click -> {
            back();
            return true;
        }, "&c&lBack");
    }

    protected void setColours(Dye outside, Dye between, Dye middle) {
        this.outside = GUIUtil.createPaneGuiElement(outside, 'd');
        this.between = GUIUtil.createPaneGuiElement(between, 'm');
        this.middle = GUIUtil.createPaneGuiElement(middle, 'c');
    }

    protected void fullCloseOnEsc() {
        this.current.setCloseAction(close -> false);
    }

    public void onReturn(Runnable onReturn) {
        this.onReturn = onReturn;
    }

    public void receiveChatMessage(String message) {}

    protected void back() {
        if (parent != null) {
            onReturn.run();
            parent.show(holder);
        } else {
            current.close();
        }
    }
}