package de.legoshi.lccore.achievement;

import de.legoshi.lccore.achievement.progress.Progress;
import de.legoshi.lccore.achievement.requirement.UnlockRequirement;
import de.legoshi.lccore.achievement.reward.Reward;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Achievement {
    private String name;
    private String id;
    private AchievementType type;
    private AchievementDifficulty difficulty;
    private int points;
    private List<UnlockRequirement> requirements;
    private List<Reward> rewards;
    private String description;

    public List<Progress> getProgress(Player player) {
        List<Progress> progressList = new ArrayList<>();
        for(UnlockRequirement requirement : requirements) {
            progressList.add(requirement.getProgress(player));
        }
        return progressList;
    }

    public boolean isUnlocked(Player player) {
        for(UnlockRequirement requirement : requirements) {
            if(!requirement.isSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

    public void giveReward(Player player) {
        for(Reward reward : rewards) {
            reward.give(player);
        }
    }
}
