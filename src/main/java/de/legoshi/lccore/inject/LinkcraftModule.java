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

        // Managers
        bind(VisibilityManager.class).singleton();
        bind(LuckPermsManager.class).singleton();
        bind(PlayerManager.class).singleton();
        bind(ChatManager.class).singleton();
        bind(PartyManager.class).singleton();
        bind(TagManager.class).singleton();
        bind(SVSManager.class).singleton();
        bind(PunishmentManager.class).singleton();
        bind(MapManager.class).singleton();
        bind(ConfigManager.class).singleton();
        bind(EssentialsManager.class).singleton();
        bind(PracticeManager.class).singleton();
        bind(CheckpointManager.class).singleton();
        bind(AchievementManager.class).singleton();

        // Database
        install(new DBModule(plugin));

        // Flow
        install(new CommandFlowModule());

        // Listeners
        bind(GeneralListener.class).singleton();
        bind(InventoryClickListener.class).singleton();
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
        bind(CommandListener.class).singleton();
        bind(TeleportListener.class).singleton();
        bind(SpawnLocationListener.class).singleton();
        bind(InteractListener.class).singleton();
        bind(ItemDropListener.class).singleton();
        bind(HangingBreakByEntityListener.class).singleton();
        bind(EntityDamageByEntityListener.class).singleton();
        bind(PlayerInteractAtEntityListener.class).singleton();

        // Services
        install(new ServiceModule());
    }
}
