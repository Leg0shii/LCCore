package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.Menu;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import team.unnamed.inject.Inject;

import java.util.Collection;

public class CommandListener implements Listener {

    @Inject private PlayerManager playerManager;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        Collection<PotionEffect> potionEffects = player.getActivePotionEffects();
        boolean nonNV = false;
        for(PotionEffect effect : potionEffects) {
            if(!effect.getType().equals(PotionEffectType.NIGHT_VISION)) {
                nonNV = true;
                break;
            }
        }

        if(nonNV) {
            String parsed = e.getMessage().split(" ")[0];
            switch (parsed) {
                case "/saves":
                case "/save":
                case "/warp":
                case "/spawn":
                case "/bonus":
                case "/maze":
                case "/chal":
                case "/challenge":
                case "/rankup":
                    playerManager.addClearEffectsOnTps(player);
                    Linkcraft.asyncLater(() -> playerManager.removeClearEffectsOnTps(player), 20L * 60);
            }
        }
    }
}
