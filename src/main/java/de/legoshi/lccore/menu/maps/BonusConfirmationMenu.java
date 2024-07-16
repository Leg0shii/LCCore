package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.LuckPermsManager;
import de.legoshi.lccore.menu.GUIPane;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

public class BonusConfirmationMenu extends GUIPane {

    @Inject private LuckPermsManager luckPermsManager;
    @Inject private CheckpointManager checkpointManager;

    private final String[] guiSetup = {
            " z     x "
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "§a§lConfirm", guiSetup);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        StaticGuiElement normal = new StaticGuiElement('z', new ItemStack(Material.ENDER_STONE), click -> {
            checkpointManager.giveCheckpointGroup(holder);
//            luckPermsManager.givePermission(holder, "lc.normal");
//            luckPermsManager.givePermission(holder, "pkcp.checkpoint");
//            luckPermsManager.givePermission(holder, "pkcp.signs");
//            luckPermsManager.giveNegativePermission(holder, "lc.pro");
//            luckPermsManager.giveNegativePermission(holder, "ps.saves");
//            luckPermsManager.giveNegativePermission(holder, "ps.save");
//            luckPermsManager.giveWorldPermission(holder, "pkprac.practice", "bonus");
//            luckPermsManager.giveWorldPermission(holder, "pkcp.checkpoint", "bonus");
            Linkcraft.consoleCommand("warp bonus " + holder.getName() + " true");
            current.close();
            return true;
        }, "§a§lNORMAL MODE",
                "§eComes with checkpoints.");

        StaticGuiElement pro = new StaticGuiElement('x', new ItemStack(Material.ENDER_PORTAL_FRAME), click -> {
//            luckPermsManager.givePermission(holder, "lc.pro");
//            luckPermsManager.givePermission(holder, "ps.save");
//            luckPermsManager.givePermission(holder, "ps.saves");
//            luckPermsManager.giveNegativePermission(holder, "pkcp.signs");
//            luckPermsManager.giveNegativePermission(holder, "pkcp.checkpoint");
//            luckPermsManager.giveNegativePermission(holder, "lc.normal");
//            luckPermsManager.giveWorldPermission(holder, "pkprac.practice", "bonus");
//            luckPermsManager.giveWorldPermission(holder, "pkcp.checkpoint", "bonus", false);
            Linkcraft.consoleCommand("warp bonus " + holder.getName());
            current.close();
            return true;
        }, "§b§lRANKUP PRO MODE",
                "§dWithout checkpoints. You get a starred rank");

        current.addElements(normal, pro);
    }
}
