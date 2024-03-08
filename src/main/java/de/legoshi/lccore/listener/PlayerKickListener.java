package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import team.unnamed.inject.Inject;

public class PlayerKickListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Linkcraft.async(() -> playerManager.playerLeave(event.getPlayer()));
    }
}
