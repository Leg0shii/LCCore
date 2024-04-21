package de.legoshi.lccore.service;

import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.MapManager;
import team.unnamed.inject.Inject;

public class ConfigService implements Service {
    @Inject private ConfigManager configManager;
    @Inject private MapManager mapManager;

    @Override
    public void start() {
        configManager.init();
    }
}
