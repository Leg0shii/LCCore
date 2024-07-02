package de.legoshi.lccore.menu.shop;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.player.display.ChatColorDTO;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChatColorShopMenu extends GUIPane {

    @Inject
    private PlayerManager playerManager;
    private List<ChatColorDTO> sortedColors = new ArrayList<>();
    private List<String> owned = new ArrayList<>();

    private final String[] guiSetup = {
            "lwwwwwwwo",
            "egggggggy",
            "ugggggggi",
            "pgggggggb",
            "rwwwqwwwc",
    };

    private final String[] noParentGuiSetup = {
            "lwwwwwwwo",
            "egggggggy",
            "ugggggggi",
            "pgggggggb",
            "rwwwwwwwc",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Chat Color Shop", parent != null ? guiSetup : noParentGuiSetup);
        this.sortedColors = new ArrayList<>(ConfigManager.chatColorsMap.values());
        sortedColors.sort(Comparator.comparing(ChatColorDTO::getCost));
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    private void loadData() {
        this.owned = playerManager.ownedColors(holder);
        current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for(ChatColorDTO color : sortedColors) {
            boolean canAfford = playerManager.canAfford(holder, color.getCost());
            boolean hasColor = owned.contains(color.getKey());
            ChatColor canAffordColour = canAfford ? ChatColor.GREEN : ChatColor.RED;
            ItemStack clone = color.getItem().clone();
            String display = ChatColor.getByChar(color.getCode()) + GUIUtil.capitalize(color.getKey());

            if(hasColor) {
                ItemUtil.addGlow(clone);
            }

            StaticGuiElement colorElement = new StaticGuiElement('g', clone, click -> {
                if(hasColor) {
                    MessageUtil.send(Message.SHOP_ALREADY_OWN, holder, display);
                    return true;
                }

                if(!canAfford) {
                    MessageUtil.send(Message.SHOP_CANT_AFFORD, holder, color.getCost() - playerManager.ppInt(holder), display);
                    return true;
                }

                playerManager.unlockColor(holder, color.getKey());
                MessageUtil.send(Message.SHOP_BOUGHT_COLOR, holder, display);
                loadData();
                current.draw();
                LCSound.COMPLETE.playLater(holder);
                return true;
            }, new GUIDescriptionBuilder()
                    .raw(display)
                    .pair("Cost", canAffordColour + "" + color.getCost() + "pp")
                    .action(GUIAction.LEFT_CLICK, "Purchase")
                    .build());

            group.addElement(colorElement);
        }

        current.addElements(group);
    }

    @Override
    protected void registerGuiElements() {
        loadData();
        current.addElements(returnToParent,
                GUIUtil.createPaneGuiElement(Dye.BLUE, 'e'),
                GUIUtil.createPaneGuiElement(Dye.PURPLE, 'u'),
                GUIUtil.createPaneGuiElement(Dye.PINK, 'p'),
                GUIUtil.createPaneGuiElement(Dye.RED, 'r'),
                GUIUtil.createPaneGuiElement(Dye.LIME, 'i'),
                GUIUtil.createPaneGuiElement(Dye.BROWN, 'b'),
                GUIUtil.createPaneGuiElement(Dye.ORANGE, 'o'),
                GUIUtil.createPaneGuiElement(Dye.YELLOW, 'y'),
                GUIUtil.createPaneGuiElement(Dye.WHITE, 'w'),
                GUIUtil.createPaneGuiElement(Dye.BLACK, 'c'));
    }
}
