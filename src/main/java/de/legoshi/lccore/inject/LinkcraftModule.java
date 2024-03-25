package de.legoshi.lccore.inject;

import de.legoshi.lccore.database.DBModule;
import de.legoshi.lccore.listener.*;
import de.legoshi.lccore.manager.*;
import de.legoshi.lccore.service.command.CommandFlowModule;
import de.legoshi.lccore.service.module.ServiceModule;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.AbstractModule;

public class LinkcraftModule extends AbstractModule {

    private final Plugin plugin;

    public LinkcraftModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);

        // Database
        install(new DBModule(plugin));

        // Flow
        install(new CommandFlowModule());

        // Managers
        bind(VisibilityManager.class).singleton();
        bind(LuckPermsManager.class).singleton();
        bind(PlayerManager.class).singleton();
        bind(ChatManager.class).singleton();
        bind(PartyManager.class).singleton();
        bind(TagManager.class).singleton();

        // Listeners
        bind(GeneralListener.class).singleton();
        bind(InventoryClickListener.class).singleton();
        bind(PkPracListener.class).singleton();
        bind(PlayerJoinListener.class).singleton();
        bind(PlayerLeaveListener.class).singleton();
        bind(CheckPointListener.class).singleton();
        bind(VanishListener.class).singleton();
        bind(ItemConsumeListener.class).singleton();
        bind(InteractEntityListener.class).singleton();
        bind(PreJoinListener.class).singleton();
        bind(PlayerKickEvent.class).singleton();
        bind(ChatListener.class).singleton();
        bind(LPListener.class).singleton();

        // Services
        install(new ServiceModule());
    }
}
