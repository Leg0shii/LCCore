package de.legoshi.lccore.menu;

import de.legoshi.lccore.util.GUIAction;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUIScrollable extends GUIPane {
    protected StaticGuiElement pageLeft;
    protected StaticGuiElement pageRight;
    protected int page = 0;
    protected int pageVolume = 0;
    protected int maxPages = 0;

    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        registerPageElements();
    }

    protected boolean isPageEmpty() {
        return maxPages + 1 == 0;
    }

    protected abstract void getPage();

    private StaticGuiElement createPageElement(char slot, String display, int increment) {
        ItemStack arrowItem = new ItemStack(Material.ARROW, page + 1 + increment);
        GUIDescriptionBuilder descriptionBuilder = new GUIDescriptionBuilder()
                .raw(display)
                .action(GUIAction.LEFT_CLICK, "1 Page")
                .action(GUIAction.SHIFT_LEFT_CLICK, "5 Pages");

        return new StaticGuiElement(slot, arrowItem, click -> {
            int newPage;
//            if(increment == 1 && page == maxPages) {
//                return true;
//            }
//
//            if(increment == -1 && page == 0) {
//                return true;
//            }

            if (click.getType().isShiftClick()) {
                newPage = Math.max(Math.min(page + (5 * increment), maxPages), 0);
            } else {
                newPage = Math.max(Math.min(page + increment, maxPages), 0);
            }

            if (newPage != page) {
                page = newPage;
                getPage();
                pageRight.setNumber(page + 2);
                pageLeft.setNumber(page + 1);
                click.getGui().setPageNumber(page);
                click.getGui().playClickSound();
            }
            return true;
        }, descriptionBuilder.build());
    }

    private void registerPageElements() {
        this.page = 0;
        this.pageLeft = createPageElement('l', "Previous Page", -1);
        this.pageRight = createPageElement('r', "Next Page", 1);
    }

    protected void noDataItem(char slot, String title) {
        GuiElementGroup group = new GuiElementGroup(slot);
        group.addElement(new StaticGuiElement(slot, new ItemStack(Material.PAPER, 1), ChatColor.RED + title));
        this.current.addElement(group);
    }

    protected void changeOption(char slotToRemove, GuiElement.Click click) {
        page = 0;
        this.current.removeElement(slotToRemove);
        getPage();
        pageLeft.setNumber(1);
        pageRight.setNumber(2);
        click.getGui().setPageNumber(0);
        click.getGui().playClickSound();
    }
}
