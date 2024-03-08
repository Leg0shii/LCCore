package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PreJoinListener implements Listener {

    private boolean lockdownMode = false;
    private String lockdownMessage = null;

    public void reloadLockdownData() {
        FileConfiguration config = Linkcraft.getPlugin().lockdownConfig.getConfig();
        lockdownMode = config.getBoolean("lockdown-mode");
        lockdownMessage = config.getString("lockdown-message");
    }

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if(lockdownMessage == null) {
            reloadLockdownData();
        }

        if(!lockdownMode) {
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
        if(!Bukkit.getWhitelistedPlayers().contains(player)) {
            event.setKickMessage(lockdownMessage);
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }
    }
}
