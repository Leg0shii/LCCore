package de.legoshi.lccore.service;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.listener.LPListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.reflections.Reflections;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.Set;

public class ListenerService implements Service {
    @Inject private Injector injector;

    @Override
    public void start() {
        register();
    }

    private void register() {
        PluginManager pluginManager = Linkcraft.getPlugin().getServer().getPluginManager();
        Reflections reflections = new Reflections("de.legoshi.lccore.listener");
        Set<Class<? extends Listener>> listeners = reflections.getSubTypesOf(Listener.class);

        for(Class<? extends Listener> listener : listeners) {
            pluginManager.registerEvents(injector.getInstance(listener), Linkcraft.getPlugin());
        }

        injector.getInstance(LPListener.class).register();
    }
}
