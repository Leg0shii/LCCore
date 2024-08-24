package de.legoshi.lccore.achievement.reward;

import de.legoshi.lccore.manager.TagManager;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

public class TagReward implements Reward {
    @Inject private TagManager tagManager;
    private String tagId;

    public void init(String tagId) {
        this.tagId = tagId;
    }

    public void give(Player player) {
        tagManager.unlockTagReward(player, tagId);
    }
}
