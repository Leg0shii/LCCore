package de.legoshi.lccore.menu.tags;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.TagManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.tag.TagDTO;
import de.legoshi.lccore.tag.TagOwnedDTO;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Inject;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TagLeaderboard extends GUIScrollablePane {

    @Inject private TagManager tagManager;

    private String tagId;
    private String tagDisplay;
    private String tagItemDescription;
    private boolean canRemoveTags;
    private List<TagOwnedDTO> owners;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lt--q---r",
    };

    public void openGui(Player player, InventoryGui parent, String tagId, String tagItemDescription, String tagDisplay) {
        super.openGui(player, parent);
        this.tagId = tagId;
        this.tagItemDescription = tagItemDescription;
        this.tagDisplay = tagDisplay;
        this.canRemoveTags = holder.hasPermission("linkcraft.tags.remove");
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Tag Owners", guiSetup);
        this.owners = tagManager.getTagLBData(tagId);

        fullCloseOnEsc();
        registerGuiElements();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        setColours();
        updateMaxPages();
        getPage();
        StaticGuiElement tag = new StaticGuiElement('t', new ItemStack(Material.NAME_TAG, 1), click -> true, tagItemDescription);
        this.current.addElements(this.pageLeft, this.pageRight, this.returnToParent, tag);
    }

    private void updateMaxPages() {
        maxPages = (int) Math.ceil((double) owners.size() / pageVolume) - 1;
    }

    private StaticGuiElement addOwner(TagOwnedDTO owned, int ownersIdx) {
        String uuid = owned.getUuid();
        Date date = owned.getDate();
        String dateStr = GUIUtil.ISOString(date).equals("1970-01-01") ? "N/A" : GUIUtil.ISOString(date);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        String name = offlinePlayer.getName();

        if(name != null) {
            GUIDescriptionBuilder validPlayerDesc = new GUIDescriptionBuilder()
                    .coloured(name, ChatColor.GREEN)
                    .pair("Collected", dateStr);

            if(canRemoveTags) {
                validPlayerDesc.action(GUIAction.SHIFT_RIGHT_CLICK, "Remove Tag");
            }

            return new StaticGuiElement('g', HeadUtil.playerHead(name), click -> deleteTag(click, uuid, name, ownersIdx), validPlayerDesc.build());
        }
        else {
            GUIDescriptionBuilder invalidPlayerDesc = new GUIDescriptionBuilder()
                    .coloured(uuid, ChatColor.RED)
                    .pair("Collected", dateStr);

            if(canRemoveTags) {
                invalidPlayerDesc.action(GUIAction.SHIFT_RIGHT_CLICK, "Remove Tag");
            }

            return new StaticGuiElement('g', new ItemStack(Material.BARRIER), click -> deleteTag(click, uuid, uuid, ownersIdx), invalidPlayerDesc.build());
        }

    }

    private boolean deleteTag(GuiElement.Click click, String uuid, String name, int ownersIdx) {
        if(!canRemoveTags) {
            return true;
        }

        if(click.getType().isShiftClick() && click.getType().isRightClick()) {
            try {
                tagManager.removeTagFrom(uuid, tagId);
                MessageUtil.send(Message.TAGS_REMOVE_TAG, holder, name, tagDisplay);
                owners.remove(ownersIdx);
                updateMaxPages();
                getPage();
                click.getGui().setPageNumber(click.getGui().getPageNumber(holder));
                click.getGui().playClickSound();
            } catch (CommandException e) {
                MessageUtil.send(e.getMessage(), holder);
            }

        }
        return true;
    }

    @Override
    protected void getPage() {

        if(isPageEmpty()) {
            noDataItem('g', "No players found");
            return;
        }

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, owners.size());
        List<TagOwnedDTO> paginatedList = owners.subList(startIndex, endIndex);

        this.current.removeElement('g');
        GuiElementGroup group = new GuiElementGroup('g');

        for(int i = 0; i < paginatedList.size(); i++) {
            TagOwnedDTO owned = paginatedList.get(i);
            int ownersIdx = startIndex + i;
            group.addElement(addOwner(owned, ownersIdx));
        }

        this.current.addElement(group);
    }
}
