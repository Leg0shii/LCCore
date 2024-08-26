package de.legoshi.lccore.achievements.reward;

import org.bukkit.entity.Player;

import java.util.Map;

public interface Reward {
    void give(Player player);
    void init(Map<?, ?> data);
}
