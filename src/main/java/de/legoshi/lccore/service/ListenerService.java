package de.legoshi.lccore.service;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.LinkcraftCommandPartModule;
import de.legoshi.lccore.manager.ListenerManager;
import de.legoshi.lccore.util.Register;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilderImpl;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.builder.AnnotatedCommandBuilderImpl;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.SimplePartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.reflections.Reflections;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.Set;

public class ListenerService implements Service {
    @Inject private ListenerManager listenerManager;
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
    }
}
