package de.legoshi.lcpractice.manager;

import de.legoshi.lcpractice.LCPractice;

public class ConfigManager {
    private final LCPractice plugin;

    public ConfigManager(LCPractice plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.playerdataConfigAccessor.saveDefaultConfig();
    }
}