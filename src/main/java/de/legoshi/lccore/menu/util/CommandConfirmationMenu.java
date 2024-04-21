package de.legoshi.lccore.menu.util;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIPane;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandConfirmationMenu extends GUIPane {
    private final String[] guiSetup = {
            "    z    "
    };

    private String itemTitle;
    private String command;

    public void openGui(Player player, InventoryGui parent, String command, String itemTitle) {
        super.openGui(player, parent);
        this.itemTitle = itemTitle;
        this.command = command;
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "§a§lConfirm", guiSetup);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement confirm = new StaticGuiElement('z', new ItemStack(Material.EMERALD_BLOCK), click -> {
            holder.performCommand(command);
            current.close();
            return true;
        }, itemTitle);

        current.addElements(confirm);
    }
}
