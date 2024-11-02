package de.legoshi.lccore.achievements.requirement;

import de.legoshi.lccore.achievements.progress.NumericProgress;
import de.legoshi.lccore.achievements.progress.Progress;
import de.legoshi.lccore.manager.PlayerManager;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Map;

public class PPAmountRequirement implements UnlockRequirement {

    @Inject private PlayerManager playerManager;
    private int requiredPP;
    @Override
    public boolean isSatisfied(Player player) {
        int currentPP = playerManager.ppInt(player);
        return currentPP >= requiredPP;
    }

    @Override
    public Progress getProgress(Player player) {
        int currentPP = playerManager.ppInt(player);
        return new NumericProgress(currentPP, requiredPP);
    }

    @Override
    public void init(Map<?, ?> data) {
        this.requiredPP = (Integer)data.get("requiredParkourPoints");
    }
}
