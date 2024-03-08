package de.legoshi.lccore.menu;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUIScrollablePane extends GUIScrollable {
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        registerPageElements();
    }

    private void registerPageElements() {
        // Number of displayed elements
        pageVolume = 21;
    }

    @Override
    protected void noDataItem(char slot, String title) {
        GuiElementGroup group = new GuiElementGroup(slot);

        for(int i = 0; i < pageVolume / 2; i++) {
            group.addElement(null);
        }
        group.addElement(new StaticGuiElement(slot, new ItemStack(Material.PAPER, 1), ChatColor.RED + title));
        this.current.addElement(group);
    }
    protected abstract boolean getPage();
}
