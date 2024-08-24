package de.legoshi.lccore.achievement.requirement;

import de.legoshi.lccore.achievement.progress.NumericProgress;
import de.legoshi.lccore.achievement.progress.Progress;
import de.legoshi.lccore.manager.TagManager;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

public class TagUnlockedAmountRequirement implements UnlockRequirement {

    @Inject private TagManager tagManager;
    private int requiredTagCount;

    public void init(int requiredTagCount) {
        this.requiredTagCount = requiredTagCount;
    }

    @Override
    public boolean isSatisfied(Player player) {
        int currentTagCount = tagManager.tagCountPlayer(player).values().stream().mapToInt(Integer::intValue).sum();
        return  currentTagCount >= requiredTagCount;
    }

    @Override
    public Progress getProgress(Player player) {
        int currentTagCount = tagManager.tagCountPlayer(player).values().stream().mapToInt(Integer::intValue).sum();
        return new NumericProgress(currentTagCount, requiredTagCount);
    }
}
