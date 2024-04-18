package de.legoshi.lccore.service.module;

import de.legoshi.lccore.service.*;
import team.unnamed.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    protected void configure() {
        bind(Service.class).named("command").to(CommandService.class).singleton();
        bind(Service.class).named("papi").to(PapiService.class).singleton();
        bind(Service.class).named("listener").to(ListenerService.class).singleton();
        bind(Service.class).to(LinkcraftService.class).singleton();
    }
}
