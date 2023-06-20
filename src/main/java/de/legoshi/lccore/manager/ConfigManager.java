package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ColorHelper;

import java.io.File;

public class ConfigManager {
    private final Linkcraft plugin;

    public ConfigManager(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        plugin.saveDefaultConfig();
        plugin.playerdataConfigAccessor.saveDefaultConfig();

        // prac
        File playerData = new File(plugin.getDataFolder(), "playerdata");
        if (!playerData.exists()) playerData.mkdir();

        // checkpoints
        File signs = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "checkpoint_signs");
        if (!signs.exists()) signs.mkdir();

        File players = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "player_checkpoint_data");
        if (!players.exists()) players.mkdir();

        ColorHelper.load();
    }
}