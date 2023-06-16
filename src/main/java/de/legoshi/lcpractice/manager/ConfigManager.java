package de.legoshi.lcpractice.manager;

import de.legoshi.lcpractice.Linkcraft;

import java.io.File;

public class ConfigManager {
    private final Linkcraft plugin;

    public ConfigManager(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.playerdataConfigAccessor.saveDefaultConfig();

        File playerData = new File(plugin.getDataFolder(), "playerdata");
        if (!playerData.exists()) playerData.mkdir();
    }
}