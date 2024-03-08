package de.legoshi.lccore.service;

import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Named;

public class LinkcraftService implements Service {
    @Inject @Named("command") private Service commandService;
    @Inject @Named("papi") private Service papiService;

    @Override
    public void start() {
        MessageUtil.log(Message.SERVICE_STARTED, true, "plugin");
        commandService.start();
        papiService.start();
    }
}
