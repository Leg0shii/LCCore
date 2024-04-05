package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import team.unnamed.inject.Inject;

import java.util.Collection;

public class TeleportListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        if(playerManager.hasClearEffectsOnTp(player)) {
            Collection<PotionEffect> potionEffects = player.getActivePotionEffects();
            for(PotionEffect potionEffect : potionEffects) {
                if(!potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
                    player.removePotionEffect(potionEffect.getType());
                }
            }
        }
    }
}
