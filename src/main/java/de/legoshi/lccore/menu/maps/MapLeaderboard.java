package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
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
import team.unnamed.inject.Inject;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MapLeaderboard extends GUIScrollablePane {

    @Inject private MapManager mapManager;
    @Inject private PlayerManager playerManager;
    @Inject private DBManager db;

    private String mapId;
    private String mapName;
    private ItemStack mapItem;
    private String mapItemDescription;
    private boolean canEditCompletions;
    private List<MapCompletedDTO> completionists;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lt--q---r",
    };

    public void openGui(Player player, InventoryGui parent, String mapId, String mapItemDescription, ItemStack mapItem, String mapName) {
        super.openGui(player, parent);
        this.mapId = mapId;
        this.mapItem = mapItem;
        this.mapName = mapName;
        this.mapItemDescription = mapItemDescription;
        this.canEditCompletions = holder.hasPermission("lc.complete");
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, mapName + " Completions", guiSetup);
        this.completionists = mapManager.getMapLBData(mapId);

        fullCloseOnEsc();
        registerGuiElements();
        this.current.show(this.holder);
    }

    @Override
    protected void registerGuiElements() {
        setColours();
        updateMaxPages();
        getPage();
        StaticGuiElement map = new StaticGuiElement('t', mapItem, click -> true, mapItemDescription);
        this.current.addElements(this.pageLeft, this.pageRight, this.returnToParent, map);
    }

    private void updateMaxPages() {
        maxPages = (int) Math.ceil((double) completionists.size() / pageVolume) - 1;
    }

    private StaticGuiElement addCompletionist(MapCompletedDTO completion, int ownersIdx) {
        String uuid = completion.getUuid();
        Date date = completion.getFirstCompletion();
        String name = completion.getName();
        Integer completions = completion.getCompletions();
        String dateStr = date == null ? null : GUIUtil.ISOString(date);
        ItemStack skull = completion.getSkull();
        if(name == null || name.isEmpty()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            name = offlinePlayer.getName();
            playerManager.updateDBName(uuid, name);
        }

        if(skull == null) {
            skull = HeadUtil.playerHead(name);
            playerManager.updateSkull(uuid, skull);
        }

        if(name != null) {
            GUIDescriptionBuilder validPlayerDesc = new GUIDescriptionBuilder()
                    .coloured(name, ChatColor.GREEN);

            if(date != null) {
                validPlayerDesc.pair("Completed", dateStr);
            }

            validPlayerDesc.pair("Completions", String.valueOf(completions));

            if(canEditCompletions) {
                validPlayerDesc.action(GUIAction.SHIFT_RIGHT_CLICK, "Remove All Completions");
            }

            String finalName = name;
            return new StaticGuiElement('g', HeadUtil.playerHead(name), click -> removeCompletion(click, uuid, finalName, ownersIdx), validPlayerDesc.build());
        }
        else {
            GUIDescriptionBuilder invalidPlayerDesc = new GUIDescriptionBuilder()
                    .coloured(uuid, ChatColor.RED);

            if(date != null) {
                invalidPlayerDesc.pair("Completed", dateStr);
            }

            invalidPlayerDesc.pair("Completions", String.valueOf(completions));

            if(canEditCompletions) {
                invalidPlayerDesc.action(GUIAction.SHIFT_RIGHT_CLICK, "Remove All Completions");
            }

            return new StaticGuiElement('g', new ItemStack(Material.BARRIER), click -> removeCompletion(click, uuid, uuid, ownersIdx), invalidPlayerDesc.build());
        }

    }

    private boolean removeCompletion(GuiElement.Click click, String uuid, String name, int ownersIdx) {
        if(!canEditCompletions) {
            return true;
        }

        if(click.getType().isShiftClick() && click.getType().isRightClick()) {
            db.delete(db.find(new PlayerCompletionId(uuid, mapId), PlayerCompletion.class));
            MessageUtil.send(Message.MAP_REMOVE_COMPLETION, holder, mapId, name);
            completionists.remove(ownersIdx);
            updateMaxPages();
            getPage();
            click.getGui().setPageNumber(click.getGui().getPageNumber(holder));
            click.getGui().playClickSound();
            MessageUtil.log(Message.MAP_REMOVE_COMPLETION_LOG, true, holder.getName(), name, mapId);
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
        int endIndex = Math.min(startIndex + pageVolume, completionists.size());
        List<MapCompletedDTO> paginatedList = completionists.subList(startIndex, endIndex);

        this.current.removeElement('g');
        GuiElementGroup group = new GuiElementGroup('g');

        for(int i = 0; i < paginatedList.size(); i++) {
            MapCompletedDTO completed = paginatedList.get(i);
            if(completed.getCompletions() > 0) {
                int ownersIdx = startIndex + i;
                group.addElement(addCompletionist(completed, ownersIdx));
            }
        }

        this.current.addElement(group);
    }
}
