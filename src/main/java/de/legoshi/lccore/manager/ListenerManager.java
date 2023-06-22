package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.listener.*;
import org.bukkit.plugin.PluginManager;
import team.unnamed.inject.Inject;

// REFACTORED
public class ListenerManager {

    @Inject private Linkcraft plugin;
    @Inject private PkPracListener pkPracListener;
    @Inject private InventoryClickListener inventoryClickListener;
    @Inject private GeneralListener generalListener;
    @Inject private PlayerJoinListener playerJoinListener;
    @Inject private PlayerLeaveListener playerLeaveListener;
    @Inject private CheckPointListener checkPointListener;
    @Inject private VanishListener vanishListener;

    public void registerEvents() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(pkPracListener, plugin);
        pluginManager.registerEvents(inventoryClickListener, plugin);
        pluginManager.registerEvents(generalListener, plugin);
        pluginManager.registerEvents(playerJoinListener, plugin);
        pluginManager.registerEvents(playerLeaveListener, plugin);
        pluginManager.registerEvents(checkPointListener, plugin);
        pluginManager.registerEvents(vanishListener, plugin);
    }
}
