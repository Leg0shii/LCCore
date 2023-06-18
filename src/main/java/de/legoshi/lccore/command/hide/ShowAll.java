package de.legoshi.lccore.command.hide;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.util.Constants;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

public class ShowAll implements CommandExecutor {
    @Inject private VisibilityManager visibilityManager;
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = Linkcraft.getInstance().getConfig();

        if(!(sender instanceof Player)) {
            String notAPlayerMessage = config.getString(Constants.NOT_A_PLAYER);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notAPlayerMessage));
            return true;
        }

        Player player = (Player)sender;
        visibilityManager.showAll(player);
        String hideAllMessage = config.getString(Constants.SHOW_ALL);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', hideAllMessage));

        return true;
    }
}
