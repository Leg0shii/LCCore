package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.menu.MapsMenu;
import de.legoshi.lccore.util.MapUpdater;
import de.legoshi.lccore.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapsReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (!sender.hasPermission("lc.mapsreload")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission!"));
                return true;
            }

            try {
                MapUpdater.update();
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to local file reload if API is down for some reason...
                MapManager.loadMaps();
            }

            sender.sendMessage(Utils.chat("&aReloaded!"));
        }

        return true;
    }
}
