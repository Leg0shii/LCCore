package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.achievements.comparators.DifficultyComparator;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.util.*;
import de.themoep.inventorygui.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class NormalAchievementMenu extends GUIScrollablePane {

    public enum AchievementOrder { DIFFICULTY, COMPLETION }
    private AchievementOrder achievementOrder = AchievementOrder.DIFFICULTY;
    public List<Achievement> achievements;
    private Player target = null;
    private String targetSuffix;
    @Inject public AchievementManager achievementManager;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lwx-q-yzr"
    };

    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No Achievements found");
            return;
        }
        List<Achievement> achievements = achievementManager.getAchievements();
        sortAchievements(achievements);

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, achievements.size());
        List<Achievement> paginatedList = achievements.subList(startIndex, endIndex);

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (Achievement achievement : paginatedList) {
            group.addElement(createAchievementItem(achievement));
        }
        this.current.addElement(group);
    }


    private final BiConsumer<GuiElement.Click, NormalAchievementMenu.AchievementOrder> achOrderSetter = (click, orderOption) -> {
        this.achievementOrder = orderOption;
        changeOption('g', click);
    };

    @Override
    protected void changeOption(char slotToRemove, GuiElement.Click click) {
        this.achievements = getAchievements();
        super.changeOption(slotToRemove, click);
    }

    private List<Achievement> getAchievements() {
        List<Achievement> achievementList = achievementManager.getAchievements();
        sortAchievements(achievementList);
        maxPages = (int) Math.ceil((double) achievementList.size() / pageVolume) - 1;
        return achievementList;
    }

    private void sortAchievements(List<Achievement> achievements) {

        switch (achievementOrder) {
            case DIFFICULTY:
                achievements.sort(new DifficultyComparator());
                break;
            case COMPLETION:

                ArrayList<Achievement> unlockedAchievements = new ArrayList<>();
                ArrayList<Achievement> lockedAchievements = new ArrayList<>();


                for (Achievement achievement : achievements) {
                    if (achievement.isUnlocked(target)) unlockedAchievements.add(achievement);
                    else lockedAchievements.add(achievement);
                }
                achievements.clear();
                achievements.addAll(unlockedAchievements);
                achievements.addAll(lockedAchievements);
                break;
        }

    }



    public void openGui(Player player, Player target, InventoryGui parent) {
        super.openGui(player, parent);
        achievements = achievementManager.getAchievements();
        targetSuffix = target.getName() + "'s ";
        if (holder == target) targetSuffix = "Your ";
        this.target = target;
        String title = targetSuffix + "Achievements";
        this.current = new InventoryGui(Linkcraft.getPlugin(), target, title, guiSetup);
        setColours(Dye.GREEN, Dye.LIME, Dye.WHITE);
        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);

    }

    @Override
    @Deprecated
    protected void registerGuiElements() {
        getPage();

        GuiStateElement achPrioritizeElement = new GuiStateElement('x',
                GUIUtil.createSelectionMenu(AchievementOrder.class, new ItemStack(Material.REDSTONE_COMPARATOR), "Prioritize", achOrderSetter, false));

        GUIDescriptionBuilder achSearchForPlayerDesc = new GUIDescriptionBuilder().raw("&9Search for a Player")
                .blank()
                .raw(ChatColor.DARK_AQUA + "Current: " + ChatColor.AQUA + target.getName())
                .blank()
                .coloured("Click to edit!", ChatColor.DARK_GRAY);

        StaticGuiElement achSearchForPlayerElement = new StaticGuiElement('w', new ItemStack(Material.COMPASS), click -> true, achSearchForPlayerDesc.build());

        achSearchForPlayerElement.setAction(click -> {
            new AnvilGUI.Builder().onComplete((completion) -> {
                Player player = Bukkit.getPlayer(completion.getText());

                if (player == null) {
                    holder.sendMessage(ChatColor.RED + "Couldn't find the Player!");
                    return null;
                }

                return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {
                    openGui(this.holder, player, this.parent);
                }));
            })
                    .onClose(close -> current.show(holder))
                    .title("Player Search")
                    .itemLeft(ItemUtil.setItemText(new ItemStack(Material.PAPER), target.getName()))
                    .plugin(Linkcraft.getPlugin())
                    .open(holder);

            return true;
        });

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

        StaticGuiElement achLeaderboardElement = new StaticGuiElement('y', new ItemStack(Material.NETHER_STAR), achLeaderboardDesc.build());

        GUIDescriptionBuilder achPointsDesc = new GUIDescriptionBuilder().raw(ChatColor.DARK_RED + targetSuffix + "Achievement Points: " + ChatColor.RED + "150")
                .blank()
                .raw(ChatColor.GREEN       + "Easy     " + ChatColor.GRAY + "--> " + ChatColor.RED + "5")
                .raw(ChatColor.GOLD        + "Medium   " + ChatColor.GRAY + "--> " + ChatColor.RED + "10")
                .raw(ChatColor.RED         + "Hard     " + ChatColor.GRAY + "--> " + ChatColor.RED + "15")
                .raw(ChatColor.DARK_PURPLE + "Extreme " + ChatColor.GRAY + "--> " + ChatColor.RED + "20")
                .raw(ChatColor.DARK_RED    + "Insane&l  " + ChatColor.GRAY + "--> " + ChatColor.RED + "25")
                .blank()
                .coloured("You gain Achievement Points by completing any achievement.", ChatColor.DARK_GRAY)
                .coloured("Completing harder Achievements will reward you with more Points.", ChatColor.DARK_GRAY);

        StaticGuiElement achPointsElement = new StaticGuiElement('z', new ItemStack(Material.ACTIVATOR_RAIL), achPointsDesc.build());

        this.current.addElements(this.pageLeft, this.pageRight, this.returnToParent, achPrioritizeElement, achSearchForPlayerElement, achLeaderboardElement, achPointsElement);
    }

    private StaticGuiElement createAchievementItem(Achievement achievement) {

        Dye dye = Dye.LIGHT_GRAY;

        if (achievement.isUnlocked(target)) {
            dye = Dye.PURPLE;
        }

        return new StaticGuiElement('-', new ItemStack(Material.INK_SACK, 1, dye.data),
                new GUIDescriptionBuilder()
                        .coloured(achievement.getName(), achievement.getDifficulty().getColor())
                        .coloured(achievement.getDescription(), ChatColor.GRAY)
                        .blank()
                        // .coloured("Achievement Points: " + ChatColor.RED + ""+ achievement.getPoints(), ChatColor.DARK_GRAY)
                        // .blank()
                        .raw(GUIUtil.colorize(achievement.getDifficulty().getAchievementDifficulty()))
                        .build());
    }
}
