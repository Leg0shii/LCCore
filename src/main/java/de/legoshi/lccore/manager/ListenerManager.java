package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.listener.*;
import org.bukkit.plugin.PluginManager;

// REFACTORED
public class ListenerManager {
    private final Linkcraft plugin;
    private final PluginManager pluginManager;

    public ListenerManager(Linkcraft plugin) {
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    public void registerEvents() {
        pluginManager.registerEvents(new PkPracListener(plugin), plugin);
        pluginManager.registerEvents(new InventoryClickListener(plugin), plugin);
        pluginManager.registerEvents(new GeneralListener(), plugin);
        pluginManager.registerEvents(new PlayerJoinListener(plugin), plugin);
        pluginManager.registerEvents(new PlayerLeaveListener(plugin), plugin);
    }
}
