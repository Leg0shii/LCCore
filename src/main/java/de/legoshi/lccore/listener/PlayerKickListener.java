package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener implements Listener {
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player p = event.getPlayer();
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getInstance(), Linkcraft.getInstance().getPlayerdataFolder(), p.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        playerDataConfig.set("lastlocation", Utils.getStringFromLocation(p.getLocation()));
        playerDataConfig.set("jumps", p.getStatistic(Statistic.JUMP));
        playerDataConfig.set("tags", DeluxeTag.getAvailableTagIdentifiers(p));
        playerData.saveConfig();
    }
}
