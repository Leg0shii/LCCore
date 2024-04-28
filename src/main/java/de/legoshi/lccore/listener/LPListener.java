package de.legoshi.lccore.listener;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.LuckPermsManager;
import de.legoshi.lccore.manager.PlayerManager;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class LPListener implements Listener {
    @Inject private LuckPermsManager luckPermsManager;
    @Inject private PlayerManager playerManager;

    public void register() {
        EventBus eventBus = luckPermsManager.getEventBus();
        eventBus.subscribe(Linkcraft.getPlugin(), NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(Linkcraft.getPlugin(), NodeRemoveEvent.class, this::onNodeRemove);
        eventBus.subscribe(Linkcraft.getPlugin(), NodeClearEvent.class, this::onNodeClear);
    }

    private void onNodeAdd(NodeAddEvent e) {
        if(!e.isUser()) {
            return;
        }

        updateIfCosmeticRelatedPerms((User)e.getTarget(), e.getNode().getKey());
    }

    private void onNodeRemove(NodeRemoveEvent e) {
        if(!e.isUser()) {
            return;
        }

        updateIfCosmeticRelatedPerms((User)e.getTarget(), e.getNode().getKey());
    }

    private void onNodeClear(NodeClearEvent e) {
        if(!e.isUser()) {
            return;
        }

        Player p = Bukkit.getPlayer(((User)e.getTarget()).getUsername());
        if(p != null) {
            playerManager.updatePlayer(p);
        }
    }

    private void updateIfCosmeticRelatedPerms(User user, String permission) {

        if(ConfigManager.keys.contains(permission)) {
            Player p = Bukkit.getPlayer(user.getUsername());
            if(p != null) {
                playerManager.updatePlayer(p);
            }
        }
    }
}
