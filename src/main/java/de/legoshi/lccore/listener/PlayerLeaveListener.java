package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import team.unnamed.inject.Inject;

// PARTLY REFACTORED
public class PlayerLeaveListener implements Listener {

    @Inject private VisibilityManager visibilityManager;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getInstance(), Linkcraft.getInstance().getPlayerdataFolder(), p.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        playerDataConfig.set("lastlocation", Utils.getStringFromLocation(p.getLocation()));
        playerDataConfig.set("jumps", p.getStatistic(Statistic.JUMP));
        playerDataConfig.set("tags", DeluxeTag.getAvailableTagIdentifiers(p));
        playerData.saveConfig();
        visibilityManager.onLeave(p);
    }
}