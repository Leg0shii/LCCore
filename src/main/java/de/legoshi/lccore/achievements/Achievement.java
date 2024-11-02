package de.legoshi.lccore.achievements;

import de.legoshi.lccore.achievements.progress.Progress;
import de.legoshi.lccore.achievements.requirement.UnlockRequirement;
import de.legoshi.lccore.achievements.reward.Reward;
import lombok.Getter;
import lombok.Setter;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class Achievement {
    private String name;
    private String id;
    private AchievementType type;
    private AchievementDifficulty difficulty;
    private int points;
    private boolean checkOnJoin; // adds UnlockRequirements to the achievement when true; on false the achievement will get unlocked upon activating some listener; DM me for a better explanation
    private List<UnlockRequirement> requirements; // for tiered achievements
    private List<Reward> rewards;
    private String description;


    public List<Progress> getProgress(Player player) {
        List<Progress> progressList = new ArrayList<>();
        for(UnlockRequirement requirement : requirements) {
            progressList.add(requirement.getProgress(player));
        }
        return progressList;
    }

    public Progress getProgress(Player player, Integer milestone) {

        UnlockRequirement requirement = requirements.get(milestone - 1);

        return requirement.getProgress(player);
    }

    public boolean isUnlocked(Player player) {
        for(UnlockRequirement requirement : requirements) {
            if(!requirement.isSatisfied(player)) {
                return false;
            }
        }
        return true;
    }

    public boolean isUnlocked(Player player, Integer milestone) {
        return requirements.get(milestone - 1).isSatisfied(player);
    }

    public void giveReward(Player player) {
        for(Reward reward : rewards) {
            reward.give(player);
        }
    }
}
