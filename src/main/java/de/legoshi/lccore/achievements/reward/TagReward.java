package de.legoshi.lccore.achievements.reward;

import de.legoshi.lccore.manager.TagManager;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Map;

public class TagReward implements Reward {
    @Inject private TagManager tagManager;
    private String tagId;

    public void init(Map<?, ?> data) {
        this.tagId = (String)data.get("tagId");
    }

    public void give(Player player) {
        tagManager.unlockTagReward(player, tagId);
    }
}
