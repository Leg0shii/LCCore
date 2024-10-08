package de.legoshi.lccore.service;

import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;

public class LinkcraftService implements Service {
    @Inject @Named("config") private Service configurationService;
    @Inject @Named("command") private Service commandService;
    @Inject @Named("papi") private Service papiService;
    @Inject @Named("listener") private Service listenerService;
    @Inject @Named("database") private Service databaseService;
    @Inject @Named("init") private Service initService;

    @Override
    public void start() {
        startService(configurationService, "Configuration");
        startService(commandService, "Command");
        startService(listenerService, "Listener");
        startService(papiService, "PAPI");
        startService(databaseService, "Database");
        startService(initService, "Initialization");
    }

    private void startService(Service service, String display) {
        long startTime = System.nanoTime();
        MessageUtil.log(Message.SERVICE_STARTING, true, display);
        service.start();
        long endTime = System.nanoTime();
        double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;
        MessageUtil.log(Message.SERVICE_STARTED, true, display, String.format("%.1f", elapsedTimeInSeconds));
    }
}
