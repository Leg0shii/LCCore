package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import team.unnamed.inject.Inject;

public class PlayerJoinListener implements Listener {
    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // OLD
        final Player p = event.getPlayer();
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), p.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();

        if (playerDataConfig.getString("lastlocation") != null) {
            final Location loc = Utils.getLocationFromString(playerDataConfig.getString("lastlocation"));
            (new BukkitRunnable() {
                public void run() {
                    p.teleport(loc);
                }
            }).runTaskLater(Linkcraft.getPlugin(), 10L);
        }

        // NEW
        Linkcraft.async(() -> playerManager.playerJoin(p));
    }
}