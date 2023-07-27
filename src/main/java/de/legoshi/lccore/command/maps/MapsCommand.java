package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.menu.MapsMenu;
import de.legoshi.lccore.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (!sender.hasPermission("lc.maps")) {
                sender.sendMessage(Utils.chat("&cInsufficient permission!"));
                return true;
            }

            MapsMenu.INVENTORY.open(player);
        }

        return true;
    }
}
