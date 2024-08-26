package de.legoshi.lccore.achievement.reward;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Reward {
    void give(Player player);
    void init(Map<?, ?> data);
}
