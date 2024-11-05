package de.legoshi.lccore.menu.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.staff.location.LocationManageCommand;
import de.legoshi.lccore.command.staff.location.types.LocationManageRecord;
import de.legoshi.lccore.command.staff.location.types.OfflinePlayerRecord;
import de.legoshi.lccore.command.staff.location.types.OnlinePlayerRecord;
import de.legoshi.lccore.command.staff.location.types.SaveLocationRecord;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.EssentialsManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.player.PlayerSave;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.io.IOException;
import java.util.List;

public class LocationManageHolder extends GUIScrollablePane {
    @Inject private ChatManager chatManager;
    @Inject private Injector injector;
    @Inject private PracticeManager practiceManager;
    @Inject private PlayerManager playerManager;
    @Inject private EssentialsManager essentialsManager;

    private List<LocationManageRecord> records;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "l-------r",
    };

    private void updateMaxPages() {
        maxPages = (int) Math.ceil((double) records.size() / pageVolume) - 1;
    }


    public void openGui(Player player, InventoryGui parent) {
        super.openGui(player, parent);
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, "Locations", guiSetup);
        setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
        fullCloseOnEsc();

        registerGuiElements();
        current.show(holder);
    }

    @Override
    protected void registerGuiElements() {
        this.records = LocationManageCommand.cached.get(holder.getUniqueId().toString());
        updateMaxPages();
        getPage();

        this.current.addElements(pageLeft, pageRight, returnToParent);
    }

    private void removeElement(GuiElement.Click click, int idx) {
        records.remove(idx);
        LocationManageCommand.cached.put(holder.getUniqueId().toString(), records);
        updateMaxPages();
        getPage();
        click.getGui().setPageNumber(click.getGui().getPageNumber(holder));
        click.getGui().playClickSound();
    }

    private StaticGuiElement addOnlineRecord(OnlinePlayerRecord record, int idx) {
        Location pracLocation = record.getPracticeLocation();
        boolean isInPrac = pracLocation != null;

        GUIDescriptionBuilder onlinePlayerDesc = new GUIDescriptionBuilder().raw(chatManager.rankName(record.getUuid()))
                .pair("Type", "Online")
                .pair("Practice", GUIUtil.trueFalseDisplay(isInPrac))
                .action(GUIAction.LEFT_CLICK, "Teleport")
                .action(GUIAction.RIGHT_CLICK, "Send to spawn");

        if(isInPrac) {
            onlinePlayerDesc.action(GUIAction.SHIFT_LEFT_CLICK, "Teleport Practice")
                    .action(GUIAction.SHIFT_RIGHT_CLICK, "Send to spawn (unprac)");
        }

        return new StaticGuiElement('g', HeadUtil.playerHead(record.getUsername()), click -> {
            switch(click.getType()) {
                case LEFT:
                    Linkcraft.sync(() -> holder.teleport(record.getCurrentLocation()));
                    break;
                case RIGHT:
                    Linkcraft.consoleCommand("warp spawn " + record.getUsername());
                    MessageUtil.send(Message.SENDING_TO_SPAWN, holder, record.getUsername());
                    removeElement(click, idx);
                    break;
                case SHIFT_LEFT:
                    Location pracLoc = record.getPracticeLocation();
                    if(pracLoc == null) {
                        MessageUtil.send(Message.PLAYER_NO_LONGER_PRAC, holder, record.getUsername());
                    } else {
                        Linkcraft.sync(() -> holder.teleport(pracLoc));
                    }
                    break;
                case SHIFT_RIGHT:
                    if(practiceManager.forceUnpractice(record.getUuid())) {
                        MessageUtil.send(Message.UNPRACTICE_FOR, holder, record.getUsername());
                    } else {
                        MessageUtil.send(Message.PLAYER_NO_LONGER_PRAC, holder, record.getUsername());
                    }

                    Linkcraft.consoleCommand("warp spawn " + record.getUsername());
                    MessageUtil.send(Message.SENDING_TO_SPAWN, holder, record.getUsername());
                    removeElement(click, idx);
                    break;
            }
            return true;
        }, onlinePlayerDesc.build());
    }


    private StaticGuiElement addOfflineRecord(OfflinePlayerRecord record, int idx) {
        Location pracLocation = record.getPracticeLocation();
        boolean isInPrac = pracLocation != null;

        GUIDescriptionBuilder offlinePlayerDesc = new GUIDescriptionBuilder().raw(chatManager.rankName(record.getUuid()))
                .pair("Type", "Offline")
                .pair("Practice", GUIUtil.trueFalseDisplay(isInPrac))
                .action(GUIAction.LEFT_CLICK, "Teleport")
                .action(GUIAction.RIGHT_CLICK, "Send to spawn");

        if(isInPrac) {
            offlinePlayerDesc.action(GUIAction.SHIFT_LEFT_CLICK, "Teleport Practice")
                    .action(GUIAction.SHIFT_RIGHT_CLICK, "Send to spawn (unprac)");
        }

        return new StaticGuiElement('g', HeadUtil.playerHead(record.getUsername()), click -> {
            switch(click.getType()) {
                case LEFT:
                    Linkcraft.sync(() -> holder.teleport(record.getCurrentLocation()));
                    break;
                case RIGHT:
                    try {
                        playerManager.setOfflinePlayerLocationNBT(record.getUuid(), essentialsManager.getWarpLocation("spawn"));
                        MessageUtil.send(Message.SENDING_TO_SPAWN, holder, record.getUsername());
                    } catch (Exception ignored) {}
                    removeElement(click, idx);
                    break;
                case SHIFT_LEFT:
                    Location pracLoc = record.getPracticeLocation();
                    if(pracLoc == null) {
                        MessageUtil.send(Message.PLAYER_NO_LONGER_PRAC, holder, record.getUsername());
                    } else {
                        Linkcraft.sync(() -> holder.teleport(pracLoc));
                    }
                    break;
                case SHIFT_RIGHT:
                    if(practiceManager.forceUnpractice(record.getUuid())) {
                        MessageUtil.send(Message.UNPRACTICE_FOR, holder, record.getUsername());
                    } else {
                        MessageUtil.send(Message.PLAYER_NO_LONGER_PRAC, holder, record.getUsername());
                    }

                    try {
                        playerManager.setOfflinePlayerLocationNBT(record.getUuid(), essentialsManager.getWarpLocation("spawn"));
                        MessageUtil.send(Message.SENDING_TO_SPAWN, holder, record.getUsername());
                    } catch (Exception ignored) {}
                    removeElement(click, idx);
                    break;
            }
            return true;
        }, offlinePlayerDesc.build());
    }

    private StaticGuiElement addSaveRecord(SaveLocationRecord record, int idx) {
        PlayerSave save = record.getSave();

        GUIDescriptionBuilder saveDesc = new GUIDescriptionBuilder().raw(save.getName())
                .pair("Type", "Save")
                .pair("Player", chatManager.rankName(record.getUuid()))
                .header("Save Info")
                .pair("Date", GUIUtil.ISOString(save.getDate()))
                .action(GUIAction.LEFT_CLICK, "Teleport")
                .action(GUIAction.RIGHT_CLICK, "Delete");


        return new StaticGuiElement('g', save.getItem(), click -> {
            switch(click.getType()) {
                case LEFT:
                    Linkcraft.sync(() -> holder.teleport(save.getLocation()));
                    break;
                case RIGHT:
                    if(playerManager.deleteSave(record.getUuid(), save.getKey())) {
                        MessageUtil.send(Message.DELETING_SAVE, holder, save.getName(), save.getKey(), record.getUsername());
                        removeElement(click, idx);
                    } else {
                        MessageUtil.send(Message.ERR_DELETING_SAVE, holder, record.getUsername());
                    }


                    break;
            }
            return true;
        }, saveDesc.build());
    }

    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No locations found");
            return;
        }

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, records.size());
        List<LocationManageRecord> paginatedList = records.subList(startIndex, endIndex);

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for(int i = 0; i < paginatedList.size(); i++) {
            LocationManageRecord record = paginatedList.get(i);
            int idx = startIndex + i;

            switch (record.getType()) {
                case ONLINE:
                    group.addElement(addOnlineRecord((OnlinePlayerRecord)record, idx));
                    break;
                case OFFLINE:
                    group.addElement(addOfflineRecord((OfflinePlayerRecord)record, idx));
                    break;
                case SAVE:
                    group.addElement(addSaveRecord((SaveLocationRecord)record, idx));
                    break;
            }
        }

        this.current.addElement(group);
    }
}
