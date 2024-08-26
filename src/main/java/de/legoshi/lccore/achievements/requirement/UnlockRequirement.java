package de.legoshi.lccore.achievements.requirement;

import de.legoshi.lccore.achievements.progress.Progress;
import org.bukkit.entity.Player;

import java.util.Map;

public interface UnlockRequirement {
    boolean isSatisfied(String player);
    Progress getProgress(String player);
    void init(Map<?, ?> data);
}
