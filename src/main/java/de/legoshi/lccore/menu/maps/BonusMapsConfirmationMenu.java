package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.LuckPermsManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.MapType;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class BonusMapsConfirmationMenu extends GUIPane {
    @Inject private Injector injector;

    private final String[] guiSetup = {
            " z  q  x "
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "§a§lSelect Mode", guiSetup);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement normal = new StaticGuiElement('z', new ItemStack(Material.ENDER_STONE), click -> {
            injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.BONUS);
            return true;
        }, "§a§lNORMAL MODE",
                "§eComes with checkpoints.");

        StaticGuiElement pro = new StaticGuiElement('x', new ItemStack(Material.ENDER_PORTAL_FRAME), click -> {
            injector.getInstance(MapsHolder.class).openGui(holder, current, MapType.BONUS_PRO);
            return true;
        }, "§b§lRANKUP PRO MODE",
                "§dWithout checkpoints. You get a starred rank");

        current.addElements(normal, pro, returnToParent);
    }
}
