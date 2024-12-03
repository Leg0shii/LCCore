package de.legoshi.lccore.manager;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EssentialsManager {

    private Essentials getEssentials() {
        return (Essentials)Bukkit.getPluginManager().getPlugin("Essentials");
    }

    public List<String> getWarpsFor(Player player) {
        Essentials ess = getEssentials();
        User user = ess.getUser(player);

        return ess.getSettings().getPerWarpPermission() && user != null
                ? ess.getWarps().getList().stream()
                .filter(warpName -> user.isAuthorized("essentials.warps." + warpName))
                .map(Object::toString)
                .collect(Collectors.toList())
                : new ArrayList<>(ess.getWarps().getList().stream().map(Object::toString).collect(Collectors.toList()));
    }

    public double getPp(String username) {
        Essentials ess = getEssentials();
        User user = ess.getOfflineUser(username);
        return user.getMoney().longValue();
    }

    public void setPp(String username, double pp) {
        Essentials ess = getEssentials();
        User user = ess.getOfflineUser(username);
        try {
            user.setMoney(BigDecimal.valueOf(pp));
        } catch (Exception ignored) {

        }
    }

    public boolean isValidWarp(String warpName) {
        Essentials ess = getEssentials();
        for(String warp : ess.getWarps().getList()) {
            if(warpName.equalsIgnoreCase(warp)) {
                return true;
            }
        }
        return false;
    }

    public Location getWarpLocation(String warpName)  {
        Essentials ess = getEssentials();
        try {
            return ess.getWarps().getWarp(warpName);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean canWarpTo(Player player, String warpName) {
        for(String warp : getWarpsFor(player)) {
            if(warpName.equalsIgnoreCase(warp)) {
                return true;
            }
        }
        return false;
    }
}
