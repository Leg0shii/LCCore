package de.legoshi.lccore.command;

import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Register
@Command(names = {"wtp"}, permission = "wtp", desc = "<world> <x> <y> <z>")
public class WorldTeleportCommand implements CommandClass {

    @Command(names = "")
    public void wtp(CommandSender sender, String worldName, String xName, String yName, String zName) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }
        Player player = (Player)sender;

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            return;
        }

        try {
            int x = Integer.parseInt(xName);
            int y = Integer.parseInt(yName);
            int z = Integer.parseInt(zName);
            Location loc = new Location(world, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
            player.teleport(loc);
        } catch (NumberFormatException e) {}
    }
}
