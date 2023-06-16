package de.legoshi.lcpractice.command;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

// PARTLY REFACTORED
public class PingtpCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.pingtp")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
            return true;
        }

        if (args.length == 0 || args.length == 1 || args.length == 2 || args.length == 3) {
            sender.sendMessage(Utils.chat("&cInvalid syntax! Please use /pingtp <player> <x> <y> <z>"));
        } else if (args.length == 4) {
            Player p = Bukkit.getPlayer(args[0]);
            int ping = (((CraftPlayer) p).getHandle()).ping;
            if (p.getPlayer() == null) {
                sender.sendMessage(Utils.chat("&cPlayer not found!"));
                return true;
            }
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            if (ping >= 234) {
                Location loc = new Location(p.getWorld(), x, y, z);
                p.teleport(loc);
                p.sendMessage(Utils.chat("&cTeleporting to ping route..."));
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.43F);
            } else {
                sender.sendMessage(Utils.chat("&cThe specified players ping is too low!"));
            }
        } else {
            sender.sendMessage(Utils.chat("&cInvalid syntax!"));
        }
        return true;
    }

}