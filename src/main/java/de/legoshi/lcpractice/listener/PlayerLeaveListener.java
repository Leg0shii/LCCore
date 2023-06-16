package de.legoshi.lcpractice.listener;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.util.ConfigAccessor;
import de.legoshi.lcpractice.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

// PARTLY REFACTORED
public class PlayerLeaveListener implements Listener {

    private final Linkcraft plugin;

    public PlayerLeaveListener(Linkcraft plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        ConfigAccessor playerData = new ConfigAccessor(this.plugin, this.plugin.getPlayerdataFolder(), p.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        playerDataConfig.set("lastlocation", Utils.getStringFromLocation(p.getLocation()));
        playerDataConfig.set("jumps", p.getStatistic(Statistic.JUMP));
        playerDataConfig.set("tags", DeluxeTag.getAvailableTagIdentifiers(p));
        playerData.saveConfig();
    }
}