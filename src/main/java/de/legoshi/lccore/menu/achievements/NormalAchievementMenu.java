package de.legoshi.lccore.menu.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.achievements.AchievementType;
import de.legoshi.lccore.achievements.comparators.DifficultyComparator;
import de.legoshi.lccore.achievements.progress.NumericProgress;
import de.legoshi.lccore.database.models.PlayerAchievementPoints;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.themoep.inventorygui.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class NormalAchievementMenu extends GUIScrollablePane {

    public enum AchievementOrder { DIFFICULTY, COMPLETION }
    private AchievementOrder achievementOrder = AchievementOrder.DIFFICULTY;
    public List<Achievement> achievements;
    private Player target = null;
    private AchievementType achievementType = null;
    private char slotChar = '1';
    private char t2Char = 'e';
    private char t1Char = 'E';
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
        }
            slotChar = '1';
            t2Char = 'e';
            t1Char = 'E';
        }
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
                    if (achievementManager.hasAchievement(target, achievement)) unlockedAchievements.add(achievement);
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

        List<PlayerAchievementPoints> list = achievementManager.getTopTenPlayerPoints();


        GUIDescriptionBuilder achLeaderboardDesc = new GUIDescriptionBuilder().raw("&6&lLea&e&lderbo&6&lard")
                .blank();

        for (int i = 0; i < 10; i++) {
            String prefix = i == 0 ? "&6&l" : (i == 1 ? "&f&l" : (i == 2 ? "&c&l" : "&7"));
            achLeaderboardDesc.raw(prefix + "#" + (i + 1) + " " + getPositionAsString(i));
        }

        StaticGuiElement achLeaderboardElement = new StaticGuiElement('y', new ItemStack(Material.NETHER_STAR), achLeaderboardDesc.build());

        GUIDescriptionBuilder achPointsDesc = new GUIDescriptionBuilder().raw(ChatColor.DARK_RED + targetSuffix + "Achievement Points: " + ChatColor.RED + achievementManager.getAchievementPoints(target))
                .blank();

        setPointsDescription(achPointsDesc);



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
                if (achievementManager.hasAchievement(target, achievement)) {
                    dye = Dye.PURPLE;
                }
                break;
            case SECRET:
                description = "???";
                if (achievementManager.hasAchievement(target, achievement)) {
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

        boolean isUnlocked = achievement.isUnlocked(target);

        Integer[] milestones = { 1, 2 };



        for (Integer milestone : milestones) {
            ItemStack bottomOfAchievementItem = new ItemStack(Material.INK_SACK, 1, (short) 12);
            if (achievement.isUnlocked(target, milestone)) {
                bottomOfAchievementItem.setType(Material.DIAMOND);
                bottomOfAchievementItem.setDurability((short) 0);
                if (isUnlocked) {
                    ItemMeta meta = bottomOfAchievementItem.getItemMeta();
                    meta.addEnchant(Enchantment.LUCK, 10, false);
                    bottomOfAchievementItem.setItemMeta(meta);
                }
            }
            StaticGuiElement element = new StaticGuiElement(getCharFromMilestone(milestone), bottomOfAchievementItem, buildDescription(milestone, getMilestoneAsRomanNumber(milestone), achievement).build());
            this.current.addElement(element);
        }

        ItemStack topOfAchievementItem = new ItemStack(Material.STAINED_GLASS, 1, (short) 3);

        if (achievement.isUnlocked(target)) {
            ItemMeta meta = topOfAchievementItem.getItemMeta();
            meta.addEnchant(Enchantment.LUCK, 10, false);
            topOfAchievementItem.setItemMeta(meta);
            topOfAchievementItem.setType(Material.DIAMOND_BLOCK);
            topOfAchievementItem.setDurability((short) 0);
        }

        StaticGuiElement topOfAchievementElement = new StaticGuiElement(slotChar, topOfAchievementItem, buildDescription(3, "III", achievement).build());

        this.current.addElements(topOfAchievementElement);

        slotChar++;
        t1Char++;
        t2Char++;
    }

    GUIDescriptionBuilder buildDescription(Integer milestone, String milestoneAsRomanNumber, Achievement achievement) {

        NumericProgress progress = (NumericProgress) achievement.getProgress(target, milestone);

        return new GUIDescriptionBuilder().raw(ChatColor.DARK_AQUA + String.valueOf(ChatColor.BOLD) + achievement.getName() + " " + milestoneAsRomanNumber + " (" + ChatColor.YELLOW + achievement.getProgress(holder, milestone).display() + ChatColor.DARK_AQUA + ChatColor.BOLD + ")")
                .blank()
                .coloured(replaceWithInt(achievement.getDescription(), progress.getGoal()), ChatColor.AQUA)
                .blank()
                .coloured("&lMilestone " + milestoneAsRomanNumber, ChatColor.DARK_AQUA);
    }

    public String replaceWithInt(String string, int i) {
        string = MessageFormat.format(string, i);
        return string;
    }

    public String getMilestoneAsRomanNumber(Integer integer) {
        switch (integer) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
        }
        return null;
    }

    public char getCharFromMilestone(Integer integer) {
        switch (integer) {
            case 1: return t1Char;
            case 2: return t2Char;
            case 3: return slotChar;
        }
        return 0;
    }

    public String getPositionAsString(int i) {
        List<PlayerAchievementPoints> list = achievementManager.getTopTenPlayerPoints();

        if (i < 0 || i >= list.size()) {
            return "N/A, &c0";
        }

        PlayerAchievementPoints playerAchievementPoints = list.get(i);
        OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(playerAchievementPoints.getPlayerId()));

        return player != null ? player.getName() + ", &c" + playerAchievementPoints.getPoints() : "Unknown Player, &c0";
    }

    public void setPointsDescription(GUIDescriptionBuilder builder) {
        switch (this.achievementType) {
            case SECRET:
            case NORMAL:
                builder.raw(ChatColor.GREEN       + "Easy     " + ChatColor.GRAY + "--> " + ChatColor.RED + "5")
                        .raw(ChatColor.GOLD        + "Medium   " + ChatColor.GRAY + "--> " + ChatColor.RED + "10")
                        .raw(ChatColor.RED         + "Hard     " + ChatColor.GRAY + "--> " + ChatColor.RED + "15")
                        .raw(ChatColor.DARK_PURPLE + "Extreme " + ChatColor.GRAY + "--> " + ChatColor.RED + "20")
                        .raw(ChatColor.DARK_RED    + "Insane&l  " + ChatColor.GRAY + "--> " + ChatColor.RED + "25")
                        .blank()
                        .raw( ChatColor.LIGHT_PURPLE + "Secret&l  " + ChatColor.GRAY + "--> " + ChatColor.RED + "10")
                        .blank()
                        .coloured("You gain Achievement Points by completing any achievement.", ChatColor.DARK_GRAY)
                        .coloured("Completing harder Achievements will reward you with more Points.", ChatColor.DARK_GRAY);
                break;
            case GRIND:
                builder.raw("&bMilestone &3I   &7--> &c5")
                       .raw("&bMilestone &3II  &7--> &c15")
                       .raw("&bMilestone &3III &7--> &c25")
                       .blank()
                       .coloured("You gain Achievement Points by reaching new Milestones.", ChatColor.DARK_GRAY)
                       .coloured("Reaching higher Milestones will reward you with more Points.", ChatColor.DARK_GRAY);
                break;
        }
        builder.blank()
                .coloured("Your Points only update after reconnecting.", ChatColor.DARK_GRAY);
    }

}
