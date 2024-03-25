package de.legoshi.lccore.menu.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.tag.TagDTO;
import de.legoshi.lccore.tag.TagRarity;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

public class TagHolder extends GUIScrollablePane {
    @Inject private Injector injector;
    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;
    @Inject private ChatManager chatManager;

    public enum TagOwnershipFilter { ALL, COLLECTED, UNCOLLECTED }
    public enum TagOrder { OWNERSHIP, RARITY, OWNER_COUNT }

    //private TagLeaderboard tagLeaderboard;

    private TagOwnershipFilter ownershipFilter = TagOwnershipFilter.ALL;
    private HashMap<Integer, Long> tagCountsCache = new HashMap<>();
    private TagRarity rarityFilter = null;
    private TagOrder tagOrder = TagOrder.OWNERSHIP;
    private TagType tagType;
    private boolean isStaff;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lo--q--fr",
    };

    private final BiConsumer<GuiElement.Click, TagOwnershipFilter> tagOwnershipSetter = (click, ownershipFilterOption) -> {
        this.ownershipFilter = ownershipFilterOption;
        changeOption('g', click);
    };

    private final BiConsumer<GuiElement.Click, TagOrder> tagOrderSetter = (click, orderOption) -> {
        this.tagOrder = orderOption;
        changeOption('g', click);
    };

    public void openGui(Player player, InventoryGui parent, TagType type, int count) {
        super.openGui(player, parent);
        this.tagType = type;
        this.current = new InventoryGui((JavaPlugin) Linkcraft.getPlugin(), player, formattedName() + " Tags " + "(" + count + ")", guiSetup);
        this.isStaff = playerManager.isStaff(holder);
        //this.tagCountsCache = tagManager.getTagCountsCache();
        fullCloseOnEsc();
        registerGuiElements();

        this.current.show(this.holder);
        //this.tagLeaderboard = injector.getInstance(TagLeaderboard.class);
    }

    @Override
    protected void registerGuiElements() {
        getPage();

        GuiStateElement sortTags = new GuiStateElement('f',
                GUIUtil.createSelectionMenu(TagOwnershipFilter.class, new ItemStack(Material.HOPPER), "Filter by", tagOwnershipSetter, false)
        );

        GuiStateElement orderTags = new GuiStateElement('o',
                GUIUtil.createSelectionMenu(TagOrder.class, new ItemStack(Material.REDSTONE_COMPARATOR), "Order by", tagOrderSetter, false)
        );

        if(tagType == null) {
            setColours(Dye.LIME, Dye.GREEN, Dye.WHITE);
        } else {

            switch (tagType) {
                case SPECIAL:
                    setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
                    break;
                case EVENT:
                    setColours(Dye.RED, Dye.ORANGE, Dye.YELLOW);
                    break;
                case VICTOR:
                    setColours(Dye.ORANGE, Dye.YELLOW, Dye.WHITE);
                    break;
                case HIDDEN:
                    setColours(Dye.PURPLE, Dye.MAGENTA, Dye.PINK);
                    break;
            }
        }

        this.current.addElements(this.pageLeft, this.pageRight, this.returnToParent, sortTags, orderTags);
    }

    @Override
    protected boolean getPage() {
        List<TagDTO> tagData = tagManager.getTags(holder, tagType, ownershipFilter, rarityFilter, tagOrder, page, pageVolume, isStaff);


        if(tagData.isEmpty()) {
            if(page == 0) {
                noDataItem('g', "No tags found");
            }
            return false;
        }

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (TagDTO tag : tagData) {
            group.addElement(addTag(tag));
        }
        this.current.addElement(group);
        return true;
    }

    private StaticGuiElement addTag(TagDTO tagData) {
        //int tagId = tagData.getTag().getId();
        String tagId = tagData.getTag().getId();
        String example = chatManager.globalChatTagExample(holder, "msg", tagData.getTag());
        GUIDescriptionBuilder base = GUIUtil.getFullTagDisplayBuilder(tagData, example, tagData.getUnlocked() != null);

        String leaderboardDisplay = base.build();
        String tagGuiActions = base.action(GUIAction.LEFT_CLICK, "Equip")
                .action(GUIAction.RIGHT_CLICK, "Tag Owners")
                .build();

        ItemStack tagItem = new ItemStack(Material.NAME_TAG, 1);

        // If the tag is collected, add enchantment glow
        if(tagData.getUnlocked() != null) {
            ItemUtil.addGlow(tagItem);
        }

        // TODO: Add some way to let the player know their current tag?
        return new StaticGuiElement('g', tagItem, click -> {
            if(click.getType().isRightClick()) {
                //this.tagLeaderboard.openGui(holder, current, tagId, leaderboardDisplay, tagName);
            } else {
                if (tagManager.hasTag(holder, tagId)) {
                    try {
                        tagManager.setTag(holder, tagId);
                        MessageUtil.send(Message.TAGS_SELECT, holder, tagGuiActions);
                        current.close();
                    } catch(CommandException e) {
                        MessageUtil.send(Message.TAGS_HAS_TAG_SET, holder);
                    }
                } else {
                    MessageUtil.send(Message.TAGS_HASNT_UNLOCKED_SELF, holder, tagGuiActions);
                }
            }
            return true;
        },
                tagGuiActions);
    }

    private String formattedName() {
        if(tagType == null) {
            return "All";
        }
        return CommonUtil.capatalize(tagType.name().toLowerCase());
    }
}
