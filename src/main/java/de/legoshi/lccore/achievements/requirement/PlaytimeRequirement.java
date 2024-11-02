package de.legoshi.lccore.achievements.requirement;

import de.legoshi.lccore.achievements.progress.NumericProgress;
import de.legoshi.lccore.achievements.progress.Progress;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Map;

public class PlaytimeRequirement implements UnlockRequirement {
    private int requiredPlaytime;
    @Override
    public boolean isSatisfied(Player player) {
        int currentPlaytime = getPlaytimeInHours(player);
        return currentPlaytime >= requiredPlaytime;
    }

    @Override
    public Progress getProgress(Player player) {
        int currentPlaytime = getPlaytimeInHours(player);
        return new NumericProgress(currentPlaytime, requiredPlaytime);
    }

    @Override
    public void init(Map<?, ?> data) {

        this.requiredPlaytime = (Integer)data.get("requiredPlaytime");
    }

    public int getPlaytimeInHours(Player player) {
        int currentPlaytime = player.getStatistic(Statistic.PLAY_ONE_TICK);
        return currentPlaytime / (20 * 3600);
    }

}
