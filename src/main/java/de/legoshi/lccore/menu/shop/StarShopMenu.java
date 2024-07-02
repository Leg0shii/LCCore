package de.legoshi.lccore.menu.shop;

import de.legoshi.lccore.Linkcraft;
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StarShopMenu extends GUIPane {

    @Inject private PlayerManager playerManager;
    private List<StarDTO> sortedStars = new ArrayList<>();
    private List<String> owned = new ArrayList<>();

    private final String[] guiSetup = {
            "dmcmdmcmd",
            "mgggggggm",
            "cgggggggc",
            "mgggggggm",
            "dmcmqmcmd",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Star Shop", guiSetup);
        setColours(Dye.ORANGE, Dye.YELLOW, Dye.WHITE);
        this.sortedStars = new ArrayList<>(ConfigManager.starDisplay.values());
        sortedStars.sort(Comparator.comparing(StarDTO::getCost));
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    private void loadData() {
        this.owned = playerManager.ownedStars(holder);
        current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for(StarDTO star : sortedStars) {
            boolean canAfford = playerManager.canAfford(holder, star.getCost());
            boolean hasStar = owned.contains(star.getKey());
            ChatColor canAffordColour = canAfford ? ChatColor.GREEN : ChatColor.RED;
            ItemStack clone = star.getItem().clone();

            if(hasStar) {
                ItemUtil.addGlow(clone);
            }

            StaticGuiElement starElement = new StaticGuiElement('g', clone, click -> {
                if(hasStar) {
                    MessageUtil.send(Message.SHOP_ALREADY_OWN, holder, star.getDisplay());
                    return true;
                }

                if(!canAfford) {
                    MessageUtil.send(Message.SHOP_CANT_AFFORD, holder, star.getCost() - playerManager.ppInt(holder), star.getDisplay());
                    return true;
                }

                playerManager.unlockStar(holder, star.getKey());
                MessageUtil.send(Message.SHOP_BOUGHT_STAR, holder, star.getDisplay());
                loadData();
                current.draw();
                LCSound.COMPLETE.playLater(holder);
                return true;
            }, new GUIDescriptionBuilder()
                    .raw(star.getColor() + GUIUtil.capitalize(star.getKey()) + " " + star.getDisplay())
                    .pair("Cost", canAffordColour + "" + star.getCost() + "pp")
                    .action(GUIAction.LEFT_CLICK, "Purchase")
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
