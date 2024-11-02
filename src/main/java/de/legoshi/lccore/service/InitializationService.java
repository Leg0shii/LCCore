package de.legoshi.lccore.service;

import de.legoshi.lccore.cosmetics.CosmeticRegistry;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.PracticeManager;
import team.unnamed.inject.Inject;

public class InitializationService implements Service {
    @Inject private PracticeManager practiceManager;
    @Inject private CheckpointManager cpManager;
    @Inject private CosmeticRegistry cosmeticRegistry;

    @Override
    public void start() {
        practiceManager.init();
        cpManager.init();
        cosmeticRegistry.init();
    }
}
