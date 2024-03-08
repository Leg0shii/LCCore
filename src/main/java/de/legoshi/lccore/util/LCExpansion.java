package de.legoshi.lccore.util;


import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;

public class LCExpansion extends PlaceholderExpansion {

    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;

    @Override
    public String getIdentifier() {
        return "lc";
    }

    @Override
    public String getAuthor() {
        return "dklink750";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        return chatManager.getPlaceholder(identifier, player);
    }
}
