package de.legoshi.lccore.menu.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.tag.TagDTO;
import de.legoshi.lccore.tag.TagMenuData;
import de.legoshi.lccore.tag.TagRarity;
import de.legoshi.lccore.tag.TagType;
import de.legoshi.lccore.tag.comparators.TagCreationComparator;
import de.legoshi.lccore.tag.comparators.TagOwnerCountComparator;
import de.legoshi.lccore.tag.comparators.TagOwnershipComparator;
import de.legoshi.lccore.tag.comparators.TagRarityComparator;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class TagHolder extends GUIScrollablePane {
    @Inject private Injector injector;
    @Inject private PlayerManager playerManager;
    @Inject private TagManager tagManager;
    @Inject private ChatManager chatManager;

    public enum TagOwnershipFilter { ALL, COLLECTED, UNCOLLECTED }
    public enum TagOrder { OWNERSHIP, CREATED, RARITY, OWNER_COUNT }

    private TagOwnershipFilter ownershipFilter = TagOwnershipFilter.ALL;
    private TagOrder tagOrder = TagOrder.OWNERSHIP;
    private TagType tagType;
    private boolean ignoreOwnershipRequirement;
    private boolean viewAllTags;
    private boolean editTags;
    private TagMenuData tagMenuData;
    private List<TagDTO> tags;

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

    @Override
    protected void changeOption(char slotToRemove, GuiElement.Click click) {
        this.tags = getTags();
        super.changeOption(slotToRemove, click);
    }

    public void openGui(Player player, InventoryGui parent, TagType type, int count, TagMenuData tagMenuData) {

        super.openGui(player, parent);
        this.tagType = type;
        this.tagMenuData = tagMenuData;
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, formattedName() + " Tags " + "(" + count + ")", guiSetup);
        this.ignoreOwnershipRequirement = player.hasPermission("linkcraft.tags.all");
        this.viewAllTags = player.hasPermission("linkcraft.tags.viewall");
        this.editTags = player.hasPermission("linkcraft.tags.edit");
        fullCloseOnEsc();
        registerGuiElements();

        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        this.tags = getTags();
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

    private List<TagDTO> getTags() {
        List<TagDTO> filteredTags = filterTags();
        sortTags(filteredTags);
        maxPages = (int) Math.ceil((double) filteredTags.size() / pageVolume) - 1;
        return filteredTags;
    }

    private List<TagDTO> filterByVisibility(List<TagDTO> tags) {
        if(!viewAllTags) {
            return tags.stream().filter(tagDTO -> tagDTO.getTag().isVisible() || tagDTO.getUnlocked() != null).collect(Collectors.toList());
        }
        return tags;
    }

    private List<TagDTO> filterByType(List<TagDTO> tags) {
        if(tagType == null) {
            return tags;
        }
        return tags.stream().filter(tagDTO -> tagDTO.getTag().getType().equals(tagType)).collect(Collectors.toList());
    }

    private void sortTags(List<TagDTO> tags) {
        Comparator<TagDTO> tagSortingMethod = null;
        switch (tagOrder) {
            case CREATED:
                tagSortingMethod = new TagCreationComparator();
                break;
            case OWNERSHIP:
                tagSortingMethod = new TagOwnershipComparator();
                break;
            case RARITY:
                tagSortingMethod = new TagRarityComparator();
                break;
            case OWNER_COUNT:
                tagSortingMethod = new TagOwnerCountComparator();
                break;
        }

        tags.sort(tagSortingMethod);
    }

    private List<TagDTO> filterTags() {
        List<TagDTO> tags = new ArrayList<>();
        switch (ownershipFilter) {
            case ALL:
                tags.addAll(tagMenuData.getOwnedTags());
                tags.addAll(tagMenuData.getUnownedTags());
                break;
            case COLLECTED:
                tags.addAll(tagMenuData.getOwnedTags());
                break;
            case UNCOLLECTED:
                tags.addAll(tagMenuData.getUnownedTags());
                break;
        }

        tags = filterByType(tags);
        tags = filterByVisibility(tags);

        return tags;
    }

    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No tags found");
            return;
        }

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, tags.size());
        List<TagDTO> paginatedList = tags.subList(startIndex, endIndex);

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (TagDTO tag : paginatedList) {
            group.addElement(addTag(tag));
        }
        this.current.addElement(group);
    }

    private StaticGuiElement addTag(TagDTO tagData) {
        String tagId = tagData.getTag().getId();
        String example = chatManager.globalChatTagExample(holder, "msg", tagData.getTag());
        GUIDescriptionBuilder base = GUIUtil.getFullTagDisplayBuilder(tagData, example, tagData.getUnlocked() != null, editTags);

        String leaderboardDisplay = base.build();

        GUIDescriptionBuilder tagGuiActions = base.action(GUIAction.LEFT_CLICK, "Equip")
                .action(GUIAction.RIGHT_CLICK, "Tag Owners");
        if(editTags) {
            tagGuiActions.action(GUIAction.SHIFT_RIGHT_CLICK, "Edit Tag");
        }

        ItemStack tagItem = new ItemStack(Material.NAME_TAG, 1);

        // If the tag is collected, add enchantment glow
        if(tagData.getUnlocked() != null) {
            ItemUtil.addGlow(tagItem);
        }

        // TODO: Add some way to let the player know their current tag?
        return new StaticGuiElement('g', tagItem, click -> {
            if(editTags && click.getType().isShiftClick() && click.getType().isRightClick()) {
                injector.getInstance(TagCreate.class).openGui(holder, current, tagData.getTag()).onReturn(() -> {
                    this.tags = getTags();
                    getPage();
                    current.draw();
                });
            }
            else if(click.getType().isRightClick()) {
                injector.getInstance(TagLeaderboard.class).openGui(holder, current, tagId, leaderboardDisplay, tagData.getTag().getDisplay());
            } else {
                if (ignoreOwnershipRequirement || tagManager.hasTag(holder, tagId)) {
                    try {
                        tagManager.setTag(holder, tagId);
                        MessageUtil.send(Message.TAGS_SELECT, holder, tagData.getTag().getDisplay());
                        current.close();
                    } catch(CommandException e) {
                        MessageUtil.send(Message.TAGS_HAS_TAG_SET, holder);
                    }
                } else {
                    MessageUtil.send(Message.TAGS_HASNT_UNLOCKED_SELF, holder, tagData.getTag().getDisplay());
                }
            }
            return true;
        },
                tagGuiActions.build());
    }

    private String formattedName() {
        if(tagType == null) {
            return "All";
        }
        return CommonUtil.capatalize(tagType.name().toLowerCase());
    }
}
