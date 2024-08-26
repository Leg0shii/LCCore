package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievement.Achievement;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.GUIUtil;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.List;

public class NormalAchievementMenu extends GUIPane {

    @Inject private AchievementManager achievementManager;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
    };

    public void openGui(Player player, Player target, InventoryGui parent) {
        super.openGui(player, parent);

        String title = target.getName() + "'s Achievements";
        this.current = new InventoryGui(Linkcraft.getPlugin(), target, title, guiSetup);
        setColours(Dye.GREEN, Dye.LIME, Dye.WHITE);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);

    }

    @Override
    protected void registerGuiElements() {

        GuiElementGroup group = new GuiElementGroup('g');

        List<Achievement> achievements = achievementManager.getAchievements();

        for (Achievement achievement : achievements) {
            group.addElement(createAchievementItem(achievement));
        }
        this.current.addElement(group);
    }

    private StaticGuiElement createAchievementItem(Achievement achievement) {
        return new StaticGuiElement('-', new ItemStack(Material.INK_SACK, 1, Dye.LIGHT_GRAY.data),
                new GUIDescriptionBuilder()
                        .coloured(achievement.getName(), achievement.getDifficulty().getColor())
                        .coloured(achievement.getDescription(), ChatColor.GRAY)
                        .blank()
                        .coloured("Achievement Points: " + ChatColor.RED + ""+ achievement.getPoints(), ChatColor.DARK_GRAY)
                        .blank()
                        .raw(GUIUtil.colorize(achievement.getDifficulty().getAchievementDifficulty()))
                        .build());
    }

}
