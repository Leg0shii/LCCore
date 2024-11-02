package de.legoshi.lccore.achievements.requirement;

import de.legoshi.lccore.achievements.progress.NumericProgress;
import de.legoshi.lccore.achievements.progress.Progress;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Map;

public class JumpCountRequirement implements UnlockRequirement{

    private int requiredJumpCount;
    @Override
    public boolean isSatisfied(Player player) {
        int currentJumpCount = player.getStatistic(Statistic.JUMP);
        return currentJumpCount >= requiredJumpCount;
    }

    @Override
    public Progress getProgress(Player player) {
        int currentJumpCount = player.getStatistic(Statistic.JUMP);
        return new NumericProgress(currentJumpCount, requiredJumpCount);
    }

    @Override
    public void init(Map<?, ?> data) {
        this.requiredJumpCount = (Integer)data.get("requiredJumpCount");
    }
}
