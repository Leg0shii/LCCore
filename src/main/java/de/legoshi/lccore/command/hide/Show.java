package de.legoshi.lccore.command.hide;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

public class Show implements CommandExecutor {
    @Inject private VisibilityManager visibilityManager;
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        FileConfiguration config = Linkcraft.getInstance().getConfig();

        if(!(sender instanceof Player)) {
            String notAPlayerMessage = config.getString(Constants.NOT_A_PLAYER);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', notAPlayerMessage));
            return true;
        }

        if(args == null || args.length < 1) {
            sender.sendMessage(ChatColor.GREEN + "/show <name>");
            return true;
        }

        Player toShow = Bukkit.getPlayer(args[0]);
        Player player = (Player)sender;

        if(toShow == null) {
            String offlineMessage = config.getString(Constants.IS_OFFLINE);
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', offlineMessage));
            player.sendMessage(Utils.format(offlineMessage, "{player}", args[0]));
            return true;
        }

        visibilityManager.showOne(player, toShow);
        String offlineMessage = config.getString(Constants.SHOW_ONE);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Utils.format(offlineMessage, "{player}", toShow.getName())));

        return true;
    }
}
