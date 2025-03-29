package de.legoshi.lccore.menu.help;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class HelpMenu extends GUIPane {

    @Inject private Injector injector;
    private final String[] guiSetup = {
            "dmmmmmmmd",
            "m   c   m",
            "dmmmmmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Help", guiSetup);
        setColours(Dye.WHITE, Dye.GRAY, Dye.BLACK);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement commandHelpMenu = new StaticGuiElement('c', new ItemStack(Material.COMMAND_MINECART), click -> {
            injector.getInstance(CommandHelpHolder.class).openGui(holder, current);
            return true;
        }, new GUIDescriptionBuilder().raw(ChatColor.YELLOW + "" + ChatColor.BOLD + "Commands")
                .build());


        current.addElements(commandHelpMenu);
    }
}
