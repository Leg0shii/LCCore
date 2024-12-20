package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapsHolder extends GUIScrollablePane {
    @Inject private MapManager mapManager;
    @Inject private DBManager db;
    @Inject private Injector injector;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "l---q---r",
    };

    private boolean canEditCompletions = false;
    private boolean isOtherPlayer = false;
    private MapType mapType;
    private List<LCMap> guiMaps;
    private List<LCMap> maps;
    private Map<String, PlayerCompletion> playerMapData;
    private PlayerRecord record = null;

    public void openGui(Player player, InventoryGui parent, MapType type) {
        super.openGui(player, parent);
        this.mapType = type;
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, formattedName() + " Courses", guiSetup);
        setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
        fullCloseOnEsc();
        this.maps = mapManager.getMaps();
        this.playerMapData = mapManager.getPlayerMapData(player.getUniqueId().toString());
        registerGuiElements();
        current.show(holder);
    }

    public void openGui(Player player, InventoryGui parent, PlayerRecord record, MapType type) {
        super.openGui(player, parent);
        this.mapType = type;
        isOtherPlayer = true;
        this.record = record;
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, record.getName() + "'s " + formattedName() + " Courses", guiSetup);
        setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
        canEditCompletions = player.hasPermission("lc.complete");
        fullCloseOnEsc();
        this.maps = mapManager.getMaps();
        this.playerMapData = mapManager.getPlayerMapData(record.getUuid());
        registerGuiElements();
        current.show(holder);
    }

    @Override
    protected void registerGuiElements() {
        this.guiMaps = getGuiMaps();
        getPage();

        this.current.addElements(pageLeft, pageRight, returnToParent);
    }

    private StaticGuiElement addMap(LCMap mapData) {
        String warpCommand = "warp " + mapData.getId();
        PlayerCompletion completion = playerMapData.get(mapData.getId());
        GUIDescriptionBuilder base = new GUIDescriptionBuilder().coloured(mapData.getName(), ChatColor.BOLD)
                .header("Map Info");
               // .coloured("/" + warpCommand, ChatColor.GRAY);

        if(mapData.getNoPrac() != null && mapData.getNoPrac()) {
            base.raw("§c§lNo Practice");
        }

        if(!mapType.equals(MapType.LEGACY)) {
            base.raw("§e§lStars§r§e: " + mapData.getStar())
                    .raw("§d§lPP§r§d: " + GUIUtil.removeTrailingZeros(mapData.getPp()));
        }

        base.raw("§c§lLength§r§c: " + mapData.getLength())
            .raw("§a§lCreator§r§a: " + mapData.getCreator());

        String leaderboardDisplay = base.build();

        if(completion != null && completion.getCompletions() > 0) {
            String first = completion.getFirst() != null ? GUIUtil.ISOString(completion.getFirst()) : "N/A";
            String latest = completion.getLatest() != null ? GUIUtil.ISOString(completion.getLatest()) : "N/A";
            base.blank()
                .header("Completion Info")
                .raw("§b§lCompletions§r§b: " + completion.getCompletions())
                .raw("§6§lFirst§r§6: " + first)
                .raw("§6§lLatest§r§6: " + latest);
        }

        if(isOtherPlayer && canEditCompletions) {
            base.action(GUIAction.LEFT_CLICK, "Add Completion");
            base.action(GUIAction.RIGHT_CLICK, "Remove All Completions");
        } else {
            base.action(GUIAction.LEFT_CLICK, "Teleport");
            base.action(GUIAction.RIGHT_CLICK, "View Completions");
        }

        ItemStack mapItem = mapData.getItem().clone();

        if(completion != null && completion.getCompletions() > 0) {
            ItemUtil.addGlow(mapItem);
        }

        return new StaticGuiElement('g', mapItem, click -> {
            if(click.getType().isLeftClick()) {
                if(isOtherPlayer && canEditCompletions) {
                    holder.performCommand("complete " + mapData.getId() + " " + record.getName() + " nd");
                    current.close();
                } else {
                    holder.performCommand(warpCommand);
                    current.close();
                }
            } else if(click.getType().isRightClick() && isOtherPlayer && canEditCompletions && playerMapData.get(mapData.getId()) != null) {
                db.delete(db.find(new PlayerCompletionId(record.getUuid(), mapData.getId()), PlayerCompletion.class));
                MessageUtil.send(Message.MAP_REMOVE_COMPLETION, holder, mapData.getId(), record.getName());
                MessageUtil.log(Message.MAP_REMOVE_COMPLETION_LOG, true, holder.getName(), record.getName(), mapData.getId());
                current.close();
            } else if(click.getType().isRightClick() && !isOtherPlayer) {
                injector.getInstance(MapLeaderboard.class).openGui(holder, current, mapData.getId(), leaderboardDisplay, mapData.getItem(), mapData.getName());
            }
            return true;
        }, base.build());
    }

    private List<LCMap> getGuiMaps() {
        List<LCMap> filterMaps = filterMaps();
        sortMaps(filterMaps);
        maxPages = (int) Math.ceil((double) filterMaps.size() / pageVolume) - 1;
        return filterMaps;
    }

    private List<LCMap> filterMaps() {
        return filterByType(maps);
    }

    private void sortMaps(List<LCMap> maps) {
        Comparator<LCMap> mapSortingMethod = new MapComparator();
        maps.sort(mapSortingMethod);
    }

    private List<LCMap> filterByType(List<LCMap> maps) {
        if(mapType == null) {
            return maps;
        }
        return maps.stream().filter(map -> map.getMapType().equals(mapType)).collect(Collectors.toList());
    }

    private String formattedName() {
        if(mapType == null) {
            return "All";
        }
        return CommonUtil.capatalize(mapType.name().toLowerCase());
    }

    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No tags found");
            return;
        }

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, guiMaps.size());
        List<LCMap> paginatedList = guiMaps.subList(startIndex, endIndex);

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (LCMap map : paginatedList) {
            if(!holder.hasPermission("essentials.warps." + map.getId())) {
                continue;
            }
            group.addElement(addMap(map));
        }

        this.current.addElement(group);
    }
}
