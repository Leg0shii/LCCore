package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
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

public class ChatColorSettingsMenu extends GUIPane {

    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;
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
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Chat Colors", parent != null ? guiSetup : noParentGuiSetup);
        this.sortedColors = new ArrayList<>(ConfigManager.chatColorsMap.values());
        sortedColors.sort(Comparator.comparing(ChatColorDTO::getCost));
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    private void loadData() {
        this.owned = playerManager.ownedColors(holder);
        String equippedName = playerManager.getEquippedColor(holder);
        current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for(ChatColorDTO color : sortedColors) {
            boolean hasColor = owned.contains(color.getKey());
            boolean equipped = color.getKey().equals(equippedName);
            ItemStack clone = color.getItem().clone();
            String display = ChatColor.getByChar(color.getCode()) + GUIUtil.capitalize(color.getKey());

            if(equipped) {
                ItemUtil.addGlow(clone);
            }

            StaticGuiElement starElement = new StaticGuiElement('g', clone, click -> {

                if(click.getType().isLeftClick()) {
                    if(!hasColor) {
                        MessageUtil.send(Message.SHOP_NOT_OWNED, holder, display);
                        return true;
                    }

                    if(equipped) {
                        MessageUtil.send(Message.SHOP_ALREADY_EQUIPPED, holder, display);
                        return true;
                    }

                    playerManager.equipColor(holder, color.getKey());
                    MessageUtil.send(Message.SHOP_EQUIPPED, holder, display);

                } else if(click.getType().isRightClick()) {
                    if(!equipped) {
                        MessageUtil.send(Message.SHOP_NOT_EQUIPPED, holder, display);
                        return true;
                    }

                    MessageUtil.send(Message.SHOP_UNEQUIPPED, holder, display);
                    playerManager.unequipColor(holder);
                }


                loadData();
                current.draw();
                return true;
            }, new GUIDescriptionBuilder()
                    .raw(display)
                    .pair("Owned", GUIUtil.trueFalseDisplay(hasColor))
                    .blank()
                    .raw(chatManager.globalChatColorExample(holder, "msg", "" + ChatColor.getByChar(color.getCode())))
                    .action(GUIAction.LEFT_CLICK, "Equip")
                    .action(GUIAction.RIGHT_CLICK, "Unequip")
                    .build());

            group.addElement(starElement);
        }

        current.addElements(group);
    }

    @Override
    protected void registerGuiElements() {
        loadData();
        current.addElements(returnToParent,
                GUIUtil.createPaneGuiElement(Dye.BLUE, 'e'),
                GUIUtil.createPaneGuiElement(Dye.LIGHT_BLUE, 'l'),
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
