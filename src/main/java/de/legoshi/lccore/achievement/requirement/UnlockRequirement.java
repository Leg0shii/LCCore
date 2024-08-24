package de.legoshi.lccore.achievement.requirement;

import de.legoshi.lccore.achievement.progress.Progress;
import org.bukkit.entity.Player;

public interface UnlockRequirement {
    boolean isSatisfied(Player player);
    Progress getProgress(Player player);
}
