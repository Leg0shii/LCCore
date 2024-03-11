package de.legoshi.lccore.database;

import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Module;

public class DBModule implements Module {
    private final Plugin plugin;

    public DBModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(DBManager.class).toInstance(new DBManager(plugin));
    }
}
