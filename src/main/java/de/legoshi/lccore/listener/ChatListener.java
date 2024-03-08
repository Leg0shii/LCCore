package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import team.unnamed.inject.Inject;

public class ChatListener implements Listener {

    @Inject private ChatManager chatManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player player = e.getPlayer();
        String message = e.getMessage();
        chatManager.sendMessageToCurrentChannel(player, message);
    }
}
