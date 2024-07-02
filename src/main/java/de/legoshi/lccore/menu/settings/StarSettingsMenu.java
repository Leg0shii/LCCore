package de.legoshi.lccore.menu.settings;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.player.display.StarDTO;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StarSettingsMenu extends GUIPane {
    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;
    private List<StarDTO> sortedStars = new ArrayList<>();
    private List<String> owned = new ArrayList<>();

    private final String[] guiSetup = {
            "dmmmmmmmd",
            "mgggggggm",
            "mgggggggm",
            "mgggggggm",
            "dmmmqmmmd",
    };

    private final String[] noParentGuiSetup = {
            "dmmmmmmmd",
            "mgggggggm",
            "mgggggggm",
            "mgggggggm",
            "dmmmmmmmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Stars", parent != null ? guiSetup : noParentGuiSetup);
        setColours(Dye.WHITE, Dye.PINK, Dye.BLACK);
        this.sortedStars = new ArrayList<>(ConfigManager.starDisplay.values());
        sortedStars.sort(Comparator.comparing(StarDTO::getCost));
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    private void loadData() {
        this.owned = playerManager.ownedStars(holder);
        String equippedName = playerManager.getEquippedStar(holder);
        current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for(StarDTO star : sortedStars) {
            boolean hasStar = owned.contains(star.getKey());
            boolean equipped = star.getKey().equals(equippedName);
            ItemStack clone = star.getItem().clone();

            if(equipped) {
                ItemUtil.addGlow(clone);
            }

            StaticGuiElement starElement = new StaticGuiElement('g', clone, click -> {

                if(click.getType().isLeftClick()) {
                    if(!hasStar) {
                        MessageUtil.send(Message.SHOP_NOT_OWNED, holder, star.getDisplay());
                        return true;
                    }

                    if(equipped) {
                        MessageUtil.send(Message.SHOP_ALREADY_EQUIPPED, holder, star.getDisplay());
                        return true;
                    }

                    playerManager.equipStar(holder, star.getKey());
                    MessageUtil.send(Message.SHOP_EQUIPPED, holder, star.getDisplay());

                } else if(click.getType().isRightClick()) {
                    if(!equipped) {
                        MessageUtil.send(Message.SHOP_NOT_EQUIPPED, holder, star.getDisplay());
                        return true;
                    }

                    MessageUtil.send(Message.SHOP_UNEQUIPPED, holder, star.getDisplay());
                    playerManager.unequipStar(holder);
                }


                loadData();
                current.draw();
                return true;
            }, new GUIDescriptionBuilder()
                    .raw(star.getColor() + GUIUtil.capitalize(star.getKey()) + " " + star.getDisplay())
                    .pair("Owned", GUIUtil.trueFalseDisplay(hasStar))
                    .blank()
                    .raw(chatManager.globalChatStarExample(holder, "msg", star.getDisplay()))
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
        current.addElements(returnToParent);
    }
}
