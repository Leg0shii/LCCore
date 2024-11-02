package de.legoshi.lccore.menu.cosmetics;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.cosmetics.Cosmetic;
import de.legoshi.lccore.cosmetics.CosmeticRegistry;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.manager.CosmeticManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.menu.achievements.NormalAchievementMenu;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.*;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CosmeticsListMenu extends GUIScrollablePane {

    @Inject CosmeticManager cosmeticManager;

    private CosmeticType cosmeticType;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lx--q--yr",
    };

    public void openGui(Player player, CosmeticType cosmeticType, InventoryGui parent) {
        super.openGui(player, parent);

        this.cosmeticType = cosmeticType;
        String formattedCosmeticType = cosmeticType.toString();
        formattedCosmeticType = formattedCosmeticType.substring(0, 1).toUpperCase() + formattedCosmeticType.substring(1).toLowerCase();
        String title = formattedCosmeticType + " Cosmetics";

        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);

        setColours(Dye.PURPLE, Dye.MAGENTA, Dye.WHITE);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        getPage();

        this.current.addElements(this.pageLeft, this.pageRight, this.returnToParent);
    }

    @Override
    protected void getPage() {
        if (isPageEmpty()) {
            noDataItem('g', "No Cosmetics found");
            return;
        }

        Set<String> cosmetics = Cosmetic.cosmeticList;

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (String cosmeticString : cosmetics) {

                Cosmetic cosmetic = cosmeticManager.getCosmeticFromString(cosmeticString);
                if (cosmetic != null && cosmetic.getType() == this.cosmeticType) {
                    group.addElement(createCosmeticItem(cosmetic));
                }

        }

        this.current.addElement(group);

        if (this.current.getElement('x') == null) {
            updateCurrentCosmeticElement();
        }
    }
    private StaticGuiElement createCosmeticItem(Cosmetic cosmetic) {

        GUIDescriptionBuilder builder = new GUIDescriptionBuilder().coloured(cosmetic.getUncoloredName(), ChatColor.RED)
                .blank()
                .blank()
                .raw(ChatColor.RED + "You haven't unlocked this!");

        StaticGuiElement element = new StaticGuiElement('g', new ItemStack(Material.INK_SACK, 1, (short) 8), builder.build());

        if (cosmeticManager.hasCosmetic(this.holder, cosmetic.getName())) {
            element = new StaticGuiElement('g', cosmetic.getDisplay());
            element.setAction(click -> {
                cosmeticManager.updatePlayerCosmetic(this.holder, this.cosmeticType, cosmetic.getName());

                this.holder.sendMessage(ChatColor.GREEN + "You successfully changed your "
                        + cosmetic.getType().toString().toLowerCase()
                        + " to " + cosmetic.getDisplayName());


                updateCurrentCosmeticElement();
                this.current.draw();

                return true;
            });
        }

        return element;
    }


    private void updateCurrentCosmeticElement() {

        this.current.removeElement('x');

        Cosmetic currentCosmetic = cosmeticManager.getCosmeticByType(this.holder, this.cosmeticType);
        ItemStack currentCosmeticItem = new ItemStack(Material.BARRIER);
        GUIDescriptionBuilder currentCosmeticBuilder = new GUIDescriptionBuilder()
                .coloured("You don't have any " + cosmeticType.toString().toLowerCase() + " selected!", ChatColor.RED);

        if (currentCosmetic != null) {
            currentCosmeticItem = currentCosmetic.getDisplay();
            currentCosmeticBuilder = new GUIDescriptionBuilder()
                    .raw(currentCosmetic.getDisplayName())
                    .blank()
                    .coloured("Right Click to unequip!", ChatColor.DARK_GRAY);
        }

        StaticGuiElement updatedCosmeticElement = new StaticGuiElement(
                'x',
                currentCosmeticItem,
                click -> {
                    if (click.getType().isRightClick()) {
                        cosmeticManager.updatePlayerCosmetic(this.holder, this.cosmeticType, "None");
                        this.holder.sendMessage(ChatColor.RED + "Your " + cosmeticType.toString().toLowerCase() + " has been removed!");

                        updateCurrentCosmeticElement();
                        this.current.draw();
                        return true;
                    }
                    return true;
                },
                currentCosmeticBuilder.build()
        );

        this.current.addElement(updatedCosmeticElement);
    }


}
