package de.legoshi.lccore.menu.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.composite.PlayerCompletionId;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.LuckPermsManager;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.GUIScrollablePane;
import de.legoshi.lccore.menu.GuiMessage;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.*;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import de.themoep.inventorygui.*;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class MapsHolder extends GUIScrollablePane {
    @Inject private MapManager mapManager;
    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;
    @Inject private LuckPermsManager lpManager;
    @Inject private DBManager db;
    @Inject private Injector injector;

    private final String[] guiSetup = {
            "ddmmcmmdd",
            "sgggggggs",
            "sgggggggs",
            "sgggggggs",
            "ddmmcmmdd",
            "lox-q-pfr",
    };

    public enum MapSort { PP, LENGTH }
    public enum MapOrder { DESCENDING, ASCENDING }
    public enum MapFilter { ALL, COMPLETED, UNCOMPLETED, NO_PRACTICE }

    private MapsHolder.MapSort mapSort = MapSort.PP;
    private MapsHolder.MapOrder mapOrder = MapOrder.ASCENDING;
    private MapsHolder.MapFilter mapFilter = MapFilter.ALL;

    private boolean canEditCompletions = false;
    private boolean isOtherPlayer = false;
    private MapType mapType;
    private List<LCMap> guiMaps;
    private List<LCMap> maps;
    private Map<String, PlayerCompletion> playerMapData;
    private PlayerRecord record = null;
    private String mapSearch = "";

    private final BiConsumer<GuiElement.Click, MapsHolder.MapSort> mapSortSetter = (click, sortOption) -> {
        this.mapSort = sortOption;
        changeOption('g', click);
    };

    private final BiConsumer<GuiElement.Click, MapsHolder.MapOrder> mapOrderSetter = (click, orderOption) -> {
        this.mapOrder = orderOption;
        changeOption('g', click);
    };

    private final BiConsumer<GuiElement.Click, MapsHolder.MapFilter> mapFilterSetter = (click, filterOption) -> {
        this.mapFilter = filterOption;
        changeOption('g', click);
    };

    public void openGui(Player player, InventoryGui parent, MapType type) {
        openGui(player, parent, type, "", null);
    }

    public void openGui(Player player, InventoryGui parent, MapType type, String search) {
        openGui(player, parent, type, search, null);
    }

    public void openGui(Player player, InventoryGui parent, PlayerRecord record, MapType type) {
        this.isOtherPlayer = true;
        this.record = record;
        this.canEditCompletions = player.hasPermission("lc.complete");
        openGui(player, parent, type, "", record);
    }

    private void openGui(Player player, InventoryGui parent, MapType type, String search, PlayerRecord record) {
        super.openGui(player, parent);
        this.mapType = type;
        this.mapSearch = search;

        String title = (record != null) ? record.getName() + "'s " + formattedName() + " Courses" : formattedName() + " Courses";
        this.current = new InventoryGui(Linkcraft.getPlugin(), player, title, guiSetup);

        setColours(Dye.BLUE, Dye.CYAN, Dye.LIGHT_BLUE);
        fullCloseOnEsc();

        this.maps = mapManager.getMaps();
        this.playerMapData = (record != null) ? mapManager.getPlayerMapData(record.getUuid()) : mapManager.getPlayerMapData(player.getUniqueId().toString());

        registerGuiElements();
        current.show(holder);
    }

    @Override
    protected void changeOption(char slotToRemove, GuiElement.Click click) {
        this.guiMaps = getGuiMaps();
        super.changeOption(slotToRemove, click);
    }

    @Override
    protected void registerGuiElements() {
        this.guiMaps = getGuiMaps();
        getPage();

        GuiStateElement filterMaps = new GuiStateElement('f',
                GUIUtil.createSelectionMenu(MapsHolder.MapFilter.class, new ItemStack(Material.HOPPER), "Filter by", mapFilterSetter, false)
        );

        GuiStateElement sortMaps = new GuiStateElement('o',
                GUIUtil.createSelectionMenu(MapsHolder.MapSort.class, new ItemStack(Material.REDSTONE_COMPARATOR), "Sort by", mapSortSetter, false)
        );

        GuiStateElement orderMaps = new GuiStateElement('x',
                GUIUtil.createSelectionMenu(MapsHolder.MapOrder.class, new ItemStack(Material.DETECTOR_RAIL), "Order by", mapOrderSetter, false)
        );

        orderMaps.setState(mapOrder.name());

        GUIDescriptionBuilder searchMapsDesc = new GUIDescriptionBuilder().raw("Search")
                .pair("Current", mapSearch)
                .action(GUIAction.LEFT_CLICK, "Edit Search (Anvil)")
                .action(GUIAction.RIGHT_CLICK, "Edit Search (Chat)")
                .action(GUIAction.SHIFT_RIGHT_CLICK, "Clear Search");

        StaticGuiElement searchMaps = new StaticGuiElement('p', new ItemStack(Material.COMPASS), click -> true, searchMapsDesc.build());

        searchMaps.setAction(click -> {
            if(click.getType().isShiftClick() && click.getType().isRightClick()) {
                mapSearch = "";
                searchMaps.setText(searchMapsDesc.pair("Current", mapSearch, 1).build());
                changeOption('g', click);
            }
            else if(click.getType().isLeftClick()) {
                new AnvilGUI.Builder().onComplete((completion) -> {
                            mapSearch = completion.getText();

                            searchMaps.setText(searchMapsDesc.pair("Current", mapSearch, 1).build());
                            changeOption('g', click);
                            LCSound.SUCCESS.playLater(holder);

                            return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {
                                current.show(holder);
                            }));
                        })
                        .onClose(close -> current.show(holder))
                        .title("Map Search")
                        .itemLeft(ItemUtil.setItemText(new ItemStack(Material.PAPER), mapSearch))
                        .plugin(Linkcraft.getPlugin())
                        .open(holder);
            } else if(click.getType().isRightClick()) {
                current.close();
                chatManager.listenForGuiMessage(holder, new GuiMessage(this, (response) -> {
                    mapSearch = response;
                    searchMaps.setText(searchMapsDesc.pair("Current", mapSearch, 1).build());
                    changeOption('g', click);
                    current.show(holder);
                }));
            }
            return true;
        });

        this.current.addElements(pageLeft, pageRight, returnToParent, searchMaps, filterMaps, orderMaps, sortMaps);
    }

    private StaticGuiElement addMap(LCMap mapData) {
        String warpCommand = "warp " + mapData.getId();
        PlayerCompletion completion = playerMapData.get(mapData.getId());
        boolean canAccess = mapManager.canAccessWarp(holder, mapData.getId());
        GUIDescriptionBuilder base = new GUIDescriptionBuilder().raw(canAccess ? ChatColor.BOLD + mapData.getName() : ChatColor.RED + "" + ChatColor.BOLD + mapData.getName())
                .header("Map Info");

        if(mapData.getNoPrac() != null && mapData.getNoPrac()) {
            base.raw("§c§lNo Practice");
        }

        if(!canAccess) {
            base.raw("§c§lLocked");
        }

        if(mapData.getMapType().equals(MapType.BONUS)) {
            base.raw("§a§lCheckpoints");
        }

        if(!mapData.getMapType().equals(MapType.LEGACY) && !mapData.getMapType().equals(MapType.MAZE)) {
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
        if(mapOrder.equals(MapOrder.DESCENDING)) {
            Collections.reverse(filterMaps);
        }
        maxPages = (int) Math.ceil((double) filterMaps.size() / pageVolume) - 1;
        return filterMaps;
    }

    private List<LCMap> filterMaps() {
        List<LCMap> filtered = new ArrayList<>();

        switch (mapFilter) {
            case ALL:
                filtered.addAll(maps);
                break;
            case COMPLETED:
                filtered.addAll(maps.stream().filter(map -> {
                    PlayerCompletion completion = playerMapData.get(map.getId());
                    return completion != null && completion.getCompletions() > 0;
                }).collect(Collectors.toList()));
                break;
            case UNCOMPLETED:
                filtered.addAll(maps.stream().filter(map -> {
                    PlayerCompletion completion = playerMapData.get(map.getId());
                    return completion == null || completion.getCompletions() == 0;
                }).collect(Collectors.toList()));
                break;
            case NO_PRACTICE:
                filtered.addAll(maps.stream().filter(map -> map.getNoPrac() != null && map.getNoPrac()).collect(Collectors.toList()));
                break;
        }

        filtered = filterByType(filtered);
        filtered = filterBySearch(filtered);


        return filtered;
    }

    private void sortMaps(List<LCMap> maps) {
        Comparator<LCMap> mapSortingMethod = null;

        switch (mapSort) {
            case PP:
                mapSortingMethod = new MapPPComparator();
                break;
            case LENGTH:
                mapSortingMethod = new MapLengthComparator();
                break;
        }
        maps.sort(mapSortingMethod);
    }

    private List<LCMap> filterByType(List<LCMap> maps) {
        if(mapType == null) {
            return maps.stream().filter(map -> !map.getMapType().equals(MapType.MISC) && !map.getMapType().equals(MapType.LEGACY)).collect(Collectors.toList());
        }
        return maps.stream().filter(map -> map.getMapType().equals(mapType)).collect(Collectors.toList());
    }

    private String formattedName() {
        if(mapType == null) {
            return "All";
        } else if(mapType.equals(MapType.BONUS_PRO)) {
            return "Bonus Pro";
        }
        return CommonUtil.capatalize(mapType.name().toLowerCase());
    }

    private boolean matchesSearch(LCMap map) {
        String mapId = map.getId().toLowerCase();
        String mapName = GUIUtil.decolorize(map.getName()).toLowerCase();
        return mapId.contains(mapSearch.toLowerCase()) || mapName.contains(mapSearch.toLowerCase());
    }

    private List<LCMap> filterBySearch(List<LCMap> maps) {
        if(mapSearch.isEmpty()) {
            return maps;
        }
        return maps.stream().filter(this::matchesSearch).collect(Collectors.toList());
    }

    @Override
    protected void getPage() {
        if(isPageEmpty()) {
            noDataItem('g', "No maps found");
            return;
        }

        int startIndex = page * pageVolume;
        int endIndex = Math.min(startIndex + pageVolume, guiMaps.size());
        List<LCMap> paginatedList = guiMaps.subList(startIndex, endIndex);

        this.current.removeElement('g');

        GuiElementGroup group = new GuiElementGroup('g');
        for (LCMap map : paginatedList) {
            boolean isMazeOrBonus = map.getMapType() != null && (map.getMapType().equals(MapType.MAZE) || map.getMapType().equals(MapType.BONUS));
            if(!isMazeOrBonus && !holder.hasPermission("essentials.warps." + map.getId())) {
                continue;
            }
            group.addElement(addMap(map));
        }

        this.current.addElement(group);
    }
}
