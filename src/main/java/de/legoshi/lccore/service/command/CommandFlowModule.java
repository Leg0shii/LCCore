package de.legoshi.lccore.service.command;

import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;
import team.unnamed.inject.Singleton;


public class CommandFlowModule extends AbstractModule {
    @Provides
    @Singleton
    public CommandManager provideCommandManager(Plugin plugin) {
        CommandManager commandManager = new BukkitCommandManager(new LinkcraftCommandManager(), plugin.getName());
        commandManager.setUsageBuilder(new LinkcraftUsageBuilder());
        commandManager.setAuthorizer(new LinkcraftAuthorizer());
        commandManager.getTranslator().setProvider(new LinkcraftTranslationProvider());
        return commandManager;
    }
}
