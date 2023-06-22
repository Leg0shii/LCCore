package de.legoshi.lccore.inject;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.listener.*;
import de.legoshi.lccore.manager.VisibilityManager;
import team.unnamed.inject.AbstractModule;

public class LinkcraftModule extends AbstractModule {

    private final Linkcraft plugin;

    public LinkcraftModule(Linkcraft plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Linkcraft.class).toInstance(plugin);
        bind(VisibilityManager.class).singleton();
        bind(GeneralListener.class).singleton();
        bind(InventoryClickListener.class).singleton();
        bind(PkPracListener.class).singleton();
        bind(PlayerJoinListener.class).singleton();
        bind(PlayerLeaveListener.class).singleton();
        bind(CheckPointListener.class).singleton();
        bind(VanishListener.class).singleton();
    }
}
