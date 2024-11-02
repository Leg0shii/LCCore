package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.ConfigAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import team.unnamed.inject.Inject;

public class PlayerJoinListener implements Listener {
    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // OLD
        Player p = event.getPlayer();
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), p.getUniqueId() + ".yml");
        playerData.getConfig();
//
//        if (playerDataConfig.getString("lastlocation") != null) {
//            final Location loc = Utils.getLocationFromString(playerDataConfig.getString("lastlocation"));
//            (new BukkitRunnable() {
//                public void run() {
//                    p.teleport(loc);
//                }
//            }).runTaskLater(Linkcraft.getPlugin(), 10L);
//        }

        // NEW
        event.setJoinMessage("");
        Linkcraft.async(() -> playerManager.playerJoin(p));
    }
}