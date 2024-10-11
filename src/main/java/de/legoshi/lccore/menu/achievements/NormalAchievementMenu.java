package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.achievements.AchievementType;
import de.legoshi.lccore.achievements.comparators.DifficultyComparator;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.util.*;
import de.themoep.inventorygui.*;
import fr.minuskube.inv.content.SlotPos;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import net.milkbowl.vault.chat.Chat;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

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
    private AchievementType achievementType = null;
    private char slotChar = '1';
    private char t1Char = 'e';
    private char t2Char = 'E';
    private String targetSuffix;
    @Inject public AchievementManager achievementManager;
    @Inject private Injector injector;
    private final String[] normalGuiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lwx-q-yzr"
    };

    private final String[] tierGuiSetup = {
            "ddmmcmmdd",
            "s1234567s",
            "sefghijks",
            "sEFGHIJKs",
            "ddmmcmmdd",
            "lwx-q-yzr"
    };

    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No Achievements found");
            return;
        }
        List<Achievement> achievements = achievementManager.getAchievementsByType(achievementType);

        sortAchievements(achievements);

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, achievements.size());
        List<Achievement> paginatedList = achievements.subList(startIndex, endIndex);

        this.current.removeElement('g');

        if (!achievementType.equals(AchievementType.GRIND)) {
            GuiElementGroup group = new GuiElementGroup('g');
            for (Achievement achievement : paginatedList) {
                group.addElement(createAchievementItem(achievement));
            }
            this.current.addElement(group);
        } else { for (Achievement achievement : paginatedList) {
            createGrindAchievementItemGroup(achievement);
        } }
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

    public void openGui(Player player, Player target, AchievementType achievementType, InventoryGui parent) {
        super.openGui(player, parent);
        this.target = target;

        this.achievementType = achievementType;
        achievements = achievementManager.getAchievements();
        targetSuffix = target.getName() + "'s ";
        if (holder == target) targetSuffix = "Your ";
        String title = targetSuffix + "Achievements";
        String[] guiSetup;

        if (!achievementType.equals(AchievementType.GRIND)) { guiSetup = normalGuiSetup; }
        else { guiSetup = tierGuiSetup; }

        this.current = new InventoryGui(Linkcraft.getPlugin(), target, title, guiSetup);

        switch (achievementType) {
            case NORMAL: setColours(Dye.GREEN, Dye.LIME, Dye.WHITE); break;
            case GRIND: setColours(Dye.BLUE, Dye.CYAN, Dye.WHITE); break;
            case SECRET: setColours(Dye.PURPLE, Dye.MAGENTA, Dye.PINK); break;
        }

        registerGuiElements();
        fullCloseOnEsc();

        this.current.show(this.holder);
    }

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
                    openGui(this.holder, player, achievementType, this.parent);
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

        GUIDescriptionBuilder achPointsDesc = new GUIDescriptionBuilder().raw(ChatColor.DARK_RED + targetSuffix + "Achievement Points: " + ChatColor.RED + "[Points]")
                .blank()
                .raw(ChatColor.GREEN       + "Easy     " + ChatColor.GRAY + "--> " + ChatColor.RED + "5")
                .raw(ChatColor.GOLD        + "Medium   " + ChatColor.GRAY + "--> " + ChatColor.RED + "10")
                .raw(ChatColor.RED         + "Hard     " + ChatColor.GRAY + "--> " + ChatColor.RED + "15")
                .raw(ChatColor.DARK_PURPLE + "Extreme " + ChatColor.GRAY + "--> " + ChatColor.RED + "20")
                .raw(ChatColor.DARK_RED    + "Insane&l  " + ChatColor.GRAY + "--> " + ChatColor.RED + "25")
                .blank()
                .coloured("You gain Achievement Points by completing any achievement.", ChatColor.DARK_GRAY)
                .coloured("Completing harder Achievements will reward you with more Points.", ChatColor.DARK_GRAY);

        StaticGuiElement achPointsElement = new StaticGuiElement('z', new ItemStack(Material.ACTIVATOR_RAIL), click -> {
            injector.getInstance(AchievementProgressionMenu.class).openGui(this.holder, target, this.current);
            return true;
        }, achPointsDesc.build());

        this.current.addElements(this.pageLeft, this.pageRight, this.returnToParent, achPrioritizeElement, achSearchForPlayerElement, achLeaderboardElement, achPointsElement);
    }

    private StaticGuiElement createAchievementItem(Achievement achievement) {

        Dye dye = Dye.LIGHT_GRAY;
        String description = null;

        switch (achievementType) {
            case NORMAL:
                description = achievement.getDescription();
                if (achievement.isUnlocked(target)) {
                    dye = Dye.PURPLE;
                }
                break;
            case SECRET:
                description = "???";
                if (achievement.isUnlocked(target)) {
                    dye = Dye.LIME;
                }
                break;
        }

        return new StaticGuiElement('-', new ItemStack(Material.INK_SACK, 1, dye.data),
                new GUIDescriptionBuilder()
                        .coloured(achievement.getName(), achievement.getDifficulty().getColor())
                        .coloured(description, ChatColor.GRAY)
                        .blank()
                        // .coloured("Achievement Points: " + ChatColor.RED + ""+ achievement.getPoints(), ChatColor.DARK_GRAY)
                        // .blank()
                        .raw(GUIUtil.colorize(achievement.getDifficulty().getAchievementDifficulty()))
                        .build());
    }

    private void createGrindAchievementItemGroup(Achievement achievement) {

        ItemStack topOfAchievementItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 3);
        ItemStack bottomOfAchievementItem = new ItemStack(Material.INK_SACK, 1, (short) 12);

        if (achievement.isUnlocked(holder)) {
            topOfAchievementItem.setType(Material.DIAMOND_BLOCK);
            bottomOfAchievementItem.setType(Material.DIAMOND);
            topOfAchievementItem.setDurability((short) 0);
            bottomOfAchievementItem.setDurability((short) 0);
        }

        GUIDescriptionBuilder achievementItemDescription = new GUIDescriptionBuilder().raw(ChatColor.DARK_AQUA + String.valueOf(ChatColor.BOLD) + achievement.getName() + " (" + ChatColor.YELLOW + "Progress" + ChatColor.DARK_AQUA + ")")
                .blank()
                .coloured(achievement.getDescription(), ChatColor.AQUA)
                .blank()
                .coloured("&l[Milestone]", ChatColor.DARK_AQUA);

        StaticGuiElement topOfAchievementElement = new StaticGuiElement(slotChar, topOfAchievementItem, achievementItemDescription.build());
        StaticGuiElement bottomOfAchievementElement = new StaticGuiElement(t1Char, bottomOfAchievementItem, achievementItemDescription.build());
        StaticGuiElement middleOfAchievementElement = new StaticGuiElement(t2Char, bottomOfAchievementItem, achievementItemDescription.build());
        this.current.addElements(topOfAchievementElement, bottomOfAchievementElement, middleOfAchievementElement);
        slotChar++;
        t1Char++;
        t2Char++;
    }
}
