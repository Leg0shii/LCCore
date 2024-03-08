package de.legoshi.lccore.menu;

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

    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        registerPageElements();
    }

    protected abstract boolean getPage();

    private void registerPageElements() {
        this.page = 0;
        this.pageLeft = new StaticGuiElement('l', new ItemStack(Material.ARROW, page + 1), (click -> {
            if(page > 0) {
                page--;
                pageRight.setNumber(page + 2);
                pageLeft.setNumber(page + 1);
                getPage();
                click.getGui().setPageNumber(click.getGui().getPageNumber(holder));
                click.getGui().playClickSound();
            }
            return true;
        }), "Previous Page");

        this.pageRight = new StaticGuiElement('r', new ItemStack(Material.ARROW, page + 2), (click -> {
            page++;
            if(getPage()) {
                pageRight.setNumber(page + 2);
                pageLeft.setNumber(page + 1);
                click.getGui().setPageNumber(click.getGui().getPageNumber(holder) + 2);
                click.getGui().playClickSound();
                click.getGui().setPageNumber(page);
            } else {
                page--;
            }
            return true;
        }), "Next Page");
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
