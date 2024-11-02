package de.legoshi.lccore.achievements.reward;

import de.legoshi.lccore.manager.LuckPermsManager;
import org.bukkit.entity.Player;

import java.util.Map;

public class PermissionReward implements Reward {

    LuckPermsManager luckPermsManager;

    private String permissions;

    @Override
    public void init(Map<?, ?> data) { this.permissions = (String)data.get("rewardPermission"); }

    @Override
    public void give(Player player) {
        luckPermsManager.givePermission(player, permissions);
    }


}
