package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.util.*;
import de.themoep.inventorygui.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NormalAchievementMenu extends GUIPane {

    AchievementManager achievementManager = AchievementManager.getInstance();

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "s-------s",
            "s-------s",
            "s-------s",
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

        GuiElementGroup group = new GuiElementGroup('-');

        List<Achievement> achievements = achievementManager.getAllAchievements();

        assert achievements != null;
        for (Achievement achievement : achievements) {
            group.addElement(createAchievementItem(achievement));
        }
        this.current.addElement(group);
    }

    private StaticGuiElement createAchievementItem(Achievement achievement) {

        GuiElementGroup group = new GuiElementGroup('-');

        ItemStack achievementItem = new ItemBuilder(Material.INK_SACK)
                .setDurability(8)
                .setName(achievement.getDifficulty().getColor() + achievement.getName())
                .setLore(ChatColor.GRAY + achievement.getDescription(), "", ChatColor.DARK_GRAY + "Achievement Points: " + ChatColor.RED + achievement.getPoints(), "", String.valueOf(achievement.getDifficulty().getAchievementDifficulty()))
                .build();

        return new StaticGuiElement('-', achievementItem);
    }

}
