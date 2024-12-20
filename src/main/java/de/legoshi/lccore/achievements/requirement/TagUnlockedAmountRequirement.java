package de.legoshi.lccore.achievements.requirement;

import de.legoshi.lccore.achievements.progress.NumericProgress;
import de.legoshi.lccore.achievements.progress.Progress;
import de.legoshi.lccore.manager.TagManager;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Map;

public class TagUnlockedAmountRequirement implements UnlockRequirement {

    @Inject private TagManager tagManager;
    private int requiredTagCount;

    public void init(Map<?, ?> data) {

        this.requiredTagCount = (Integer)data.get("requiredTagCount");
    }

    @Override
    public boolean isSatisfied(Player player) {
        int currentTagCount = tagManager.tagCountPlayer(player).values().stream().mapToInt(Integer::intValue).sum();
        return currentTagCount >= requiredTagCount;
    }

    @Override
    public Progress getProgress(Player player) {
        int currentTagCount = tagManager.tagCountPlayer(player).values().stream().mapToInt(Integer::intValue).sum();
        return new NumericProgress(currentTagCount, requiredTagCount);
    }

}
