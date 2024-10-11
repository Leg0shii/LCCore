package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;


public class AchievementProgressionMenu extends GUIScrollablePane {

    private Player target;
    @Inject private AchievementManager achievementManager;
    @Inject private Injector injector;

    String[] guiSetup = {
            "---------",
            "ppppppppp",
            "-x--y--z-"
    };

    public void openGui(Player player, Player target, InventoryGui parent) {
        super.openGui(player, parent);
        this.target = target;
        String title = "Achievement Progress";

        this.current = new InventoryGui(Linkcraft.getPlugin(), target, title, guiSetup);
        setColours(Dye.GREEN, Dye.LIME, Dye.WHITE);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

    @Override
    protected void getPage() {

        int maxAchievementPoints = 0;
        int personalAchievementPoints = 0;
        for (Achievement achievement : achievementManager.getAchievements()) {
            maxAchievementPoints = maxAchievementPoints + achievement.getPoints();
            if (achievement.isUnlocked(target)) {
                personalAchievementPoints = personalAchievementPoints + achievement.getPoints();
            }
        }
        int personalAchievementPointsInPercent = Math.round(((float) personalAchievementPoints / maxAchievementPoints) * 100);
        int numberOfCompletedGoals = (int) (double) (personalAchievementPointsInPercent / 10);

        GuiElementGroup group = new GuiElementGroup('p');
        for (int i = 0; i < 9; i++ ) {
            group.addElement(createProgressPaneItem(numberOfCompletedGoals, personalAchievementPointsInPercent));
            numberOfCompletedGoals--;
        }
        this.current.addElement(group);
    }

    @Override
    protected void registerGuiElements() {
        getPage();

        GUIDescriptionBuilder achLeaderboardDesc = new GUIDescriptionBuilder().raw("&6&lLea&e&lderbo&6&lard")
                .blank()
                .raw("&6&l#1 KyrohPaneUp, &c150")
                .raw("&f&l#2 Dkayee, &c149 ")
                .raw("&c&l#3 i")
                .raw("&7#4 can't")
                .raw("&7#5 do")
                .raw("&7#6 this")
                .raw("&7#7 yet")
                .raw("&7#8 null")
                .raw("&7#9 null")
                .raw("&7#10 null");

        StaticGuiElement leaderboardElement = new StaticGuiElement('x', new ItemStack(Material.NETHER_STAR), achLeaderboardDesc.build());

        GUIDescriptionBuilder viewAchievementsDescription = new GUIDescriptionBuilder().raw(ChatColor.GOLD + "View your Achievements");

        StaticGuiElement viewAchievementsElement = new StaticGuiElement('z', new ItemStack(Material.GOLD_INGOT), click -> {
            injector.getInstance(AchievementMenu.class).openGui(this.holder, this.holder, null);
            return true;
        }, viewAchievementsDescription.build());

        this.current.addElements(leaderboardElement, viewAchievementsElement);
    }

    private StaticGuiElement createProgressPaneItem(int i, int percent) {
        ChatColor chatColor = ChatColor.RED;
        String completion = "GOAL NOT COMPLETED";
        int durability = 14;
        if (i > 0) {
            chatColor = ChatColor.GREEN;
            completion = "GOAL COMPLETED";
            durability = 5;
        }

        GUIDescriptionBuilder progressPaneDescription = new GUIDescriptionBuilder().raw(chatColor + String.valueOf(ChatColor.BOLD) + "Progress: " + percent + "%")
                .blank()
                .raw(ChatColor.DARK_GRAY + "Reward: " + "&d[Reward]")
                .blank()
                .raw(chatColor + String.valueOf(ChatColor.BOLD) + completion);
        ItemStack paneItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) durability);

        if (percent == 100) {
            ItemMeta itemMeta = paneItem.getItemMeta();
            itemMeta.addEnchant(Enchantment.LUCK, 10, false);
            paneItem.setItemMeta(itemMeta);
        }
        return new StaticGuiElement('p', paneItem, progressPaneDescription.build());

    }
}
