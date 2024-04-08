package de.legoshi.lccore.menu.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.GUIPane;
import de.legoshi.lccore.tag.TagMenuData;
import de.legoshi.lccore.tag.TagRarity;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.util.Dye;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.HeadUtil;
import de.legoshi.lccore.util.ItemUtil;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.HashMap;

public class TagMenu extends GUIPane {

    @Inject private Injector injector;
    @Inject private TagManager tagManager;
    private HashMap<TagType, Integer> tagCount;
    private HashMap<TagType, Integer> playerTagCount;
    private HashMap<TagType, Integer> playerUnobtainableTagCount;

    private HashMap<TagRarity, Integer> tagRarityCount;
    private HashMap<TagRarity, Integer> playerRarityTagCount;
    private HashMap<TagRarity, Integer> playerUnobtainableRarityTagCount;

    private TagMenuData tagMenuData;
    private int tagCountTotal;
    private int playerTagCountTotal;
    private int totalUnobtainableCount;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "s v   w s",
            "s   z   s",
            "s x   y s",
            "ddmmcmmdd",
            "    r    ",
    };

    @Override
    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.tagCount = tagManager.tagCounts();
        this.playerTagCount = tagManager.tagCountPlayer(player);
        this.playerUnobtainableTagCount = tagManager.tagCountPlayerUnobtainable(player);
        this.tagRarityCount = tagManager.tagRarityCounts();
        this.playerRarityTagCount = tagManager.tagRarityCountPlayer(player);
        this.playerUnobtainableRarityTagCount = tagManager.tagRarityCountPlayerUnobtainable(player);
        this.tagMenuData = tagManager.getTagMenuData(player);
        this.tagCountTotal = tagCount.values().stream().mapToInt(Integer::intValue).sum() + playerUnobtainableTagCount.values().stream().mapToInt(Integer::intValue).sum();
        this.totalUnobtainableCount = playerUnobtainableTagCount.values().stream().mapToInt(Integer::intValue).sum();
        this.playerTagCountTotal = playerTagCount.values().stream().mapToInt(Integer::intValue).sum();


        String title = "Tag Menu (" + playerTagCountTotal + "/" + tagCountTotal + ")";
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);
        setColours(Dye.BROWN, Dye.ORANGE, Dye.YELLOW);
        registerGuiElements();
        fullCloseOnEsc();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        GUIDescriptionBuilder hiddenBuilder = new GUIDescriptionBuilder()
                .raw("&d&lHidden Tags" + getMenuTitle(TagType.HIDDEN));

        int hiddenUnobtainable = playerUnobtainableTagCount.get(TagType.HIDDEN);
        if(hiddenUnobtainable > 0) {
            hiddenBuilder.blank().raw("§c" + hiddenUnobtainable + " Unobtainable Owned");
        }

        StaticGuiElement hiddenElement = new StaticGuiElement('v', HeadUtil.hiddenHead, click -> {
            injector.getInstance(TagHolder.class).openGui(this.holder, this.current, TagType.HIDDEN, playerTagCount.get(TagType.HIDDEN), tagMenuData);
            return true;
        }, hiddenBuilder.build());

        GUIDescriptionBuilder victorBuilder = new GUIDescriptionBuilder()
                .raw("&6&lVictor Tags" + getMenuTitle(TagType.VICTOR));

        int victorUnobtainable = playerUnobtainableTagCount.get(TagType.VICTOR);
        if(victorUnobtainable > 0) {
            victorBuilder.blank().raw("§c" + victorUnobtainable + " Unobtainable Owned");
        }

        StaticGuiElement victorElement = new StaticGuiElement('w', HeadUtil.victorHead, click -> {
            injector.getInstance(TagHolder.class).openGui(this.holder, this.current, TagType.VICTOR, playerTagCount.get(TagType.VICTOR), tagMenuData);
            return true;
        }, victorBuilder.build());

        GUIDescriptionBuilder eventBuilder = new GUIDescriptionBuilder()
                .raw("&c&lEvent Tags" + getMenuTitle(TagType.EVENT));

        int eventUnobtainable = playerUnobtainableTagCount.get(TagType.EVENT);
        if(eventUnobtainable > 0) {
            eventBuilder.blank().raw("§c" + eventUnobtainable + " Unobtainable Owned");
        }

        StaticGuiElement eventElement = new StaticGuiElement('x', HeadUtil.getSeasonalHead(), click -> {
            injector.getInstance(TagHolder.class).openGui(this.holder, this.current, TagType.EVENT, playerTagCount.get(TagType.EVENT), tagMenuData);
            return true;
        }, eventBuilder.build());

        GUIDescriptionBuilder specialBuilder = new GUIDescriptionBuilder()
                .raw("&3&lSpecial Tags" + getMenuTitle(TagType.SPECIAL));

        int specialUnobtainable = playerUnobtainableTagCount.get(TagType.SPECIAL);
        if(specialUnobtainable > 0) {
            specialBuilder.blank().raw("§c" + specialUnobtainable + " Unobtainable Owned");
        }

        StaticGuiElement specialElement = new StaticGuiElement('y', HeadUtil.specialHead, click -> {
            injector.getInstance(TagHolder.class).openGui(this.holder, this.current, TagType.SPECIAL, playerTagCount.get(TagType.SPECIAL), tagMenuData);
            return true;
        }, specialBuilder.build());

        ItemStack allTagsItem = new ItemStack(Material.NAME_TAG);

        if(tagCountTotal == playerTagCountTotal) {
            ItemUtil.addGlow(allTagsItem);
        }

        GUIDescriptionBuilder allBuilder = new GUIDescriptionBuilder()
                .raw("&7&lAll Tags" + " (" + playerTagCountTotal + "/" + tagCountTotal + ")")
                .blank();

        for(TagRarity rarity : TagRarity.values()) {
            allBuilder.pairCustom(rarity.display, " §7» §r", rarity.color + "" + playerRarityTagCount.get(rarity) + "/" + (tagRarityCount.get(rarity) + playerUnobtainableRarityTagCount.get(rarity)));
        }

        if(totalUnobtainableCount > 0) {
            allBuilder.blank().raw("§c" + totalUnobtainableCount + " Unobtainable Owned");
        }

        StaticGuiElement allElement = new StaticGuiElement('z', allTagsItem, click -> {
            injector.getInstance(TagHolder.class).openGui(this.holder, this.current, null, playerTagCountTotal, tagMenuData);
            return true;
        }, allBuilder.build());



        StaticGuiElement resetTag = new StaticGuiElement('r', HeadUtil.resetHead, click -> {
            if(tagManager.hasTagSelected(holder)) {
                tagManager.unsetTag(holder);
                MessageUtil.send(Message.TAGS_UNSET_TAG, holder);
                current.close();
            } else {
                MessageUtil.send(Message.TAGS_UNSET_NONE, holder);
            }

            return true;
        }, "§cRemove tag");

        current.addElements(hiddenElement, victorElement, eventElement, specialElement, allElement, resetTag);
    }

    private String getMenuTitle(TagType type) {
        return " (" + playerTagCount.get(type) + "/" + (tagCount.get(type) + playerUnobtainableTagCount.get(type)) + ")";
    }
}
