package de.legoshi.lccore.menu.shop;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.LuckPermsManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.player.display.CommandShopDTO;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommandShopMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private LuckPermsManager lpManager;
    private List<CommandShopDTO> sortedCommands = new ArrayList<>();
    private final String[] guiSetup = {
            "dmmmmmmmd",
            "mgggggggm",
            "mgggggggm",
            "mgggggggm",
            "dmmmqmmmd",
    };


    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Command Shop", guiSetup);
        this.sortedCommands = new ArrayList<>(ConfigManager.commandShopMap.values());
        sortedCommands.sort(Comparator.comparing(CommandShopDTO::getCost));
        setColours(Dye.WHITE, Dye.GRAY, Dye.BLACK);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    private void loadData() {
        current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for(CommandShopDTO command : sortedCommands) {
            ItemStack item = new ItemStack(Material.COMMAND_MINECART);
            boolean canAfford = playerManager.canAfford(holder, command.getCost());
            boolean owns = command.getPermissions().stream().allMatch(perm -> holder.hasPermission(perm));

            ChatColor canAffordColour = canAfford ? ChatColor.GREEN : ChatColor.RED;

            if(owns) {
                ItemUtil.addGlow(item);
            }

            StaticGuiElement commandElement = new StaticGuiElement('g', item, click -> {
                if(owns) {
                    MessageUtil.send(Message.SHOP_ALREADY_OWN, holder, command.getDisplay());
                    return true;
                }

                if(!canAfford) {
                    MessageUtil.send(Message.SHOP_CANT_AFFORD, holder, command.getCost() - playerManager.ppInt(holder), command.getDisplay());
                    return true;
                }

                for(String perm : command.getPermissions()) {
                    if(!holder.hasPermission(perm)) {
                        lpManager.givePermission(holder, perm);
                    }
                }

                MessageUtil.send(Message.SHOP_BOUGHT_COMMAND, holder, command.getDisplay());
                loadData();
                current.draw();
                LCSound.COMPLETE.playLater(holder);
                return true;
            }, new GUIDescriptionBuilder()
                    .raw(command.getDisplay())
                    .pair("Cost", canAffordColour + "" + command.getCost() + "pp")
                    .coloured(GUIUtil.wrap(ChatColor.BLUE + command.getDescription()), ChatColor.BLUE)
                    .action(GUIAction.LEFT_CLICK, "Purchase")
                    .build());

            group.addElement(commandElement);
        }

        current.addElements(group);
    }

    @Override
    protected void registerGuiElements() {
        loadData();
        current.addElements(returnToParent);
    }
}
