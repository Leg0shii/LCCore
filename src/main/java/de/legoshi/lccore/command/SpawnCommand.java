package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Register
@Command(names = {"spawn"}, permission = "spawn", desc = "")
public class SpawnCommand implements CommandClass {

    @Command(names = "")
    public void spawn(CommandSender sender, @OptArg String playerName) {
        if(sender instanceof ConsoleCommandSender) {
            if(playerName != null) {
                Player toTp = Bukkit.getPlayer(playerName);
                if(toTp != null) {
                    Linkcraft.fireMapChangeEvent(toTp);
                    Linkcraft.consoleCommand("essentialsspawn:spawn " + playerName);
                }
            }
            return;
        }

        if(sender instanceof Player) {
            Player playerSender = (Player)sender;

            if(playerName != null && MessageUtil.hasPerm(playerSender, "linkcraft.spawn.others")) {
                Player toTp = Bukkit.getPlayer(playerName);
                if(toTp != null) {
                    Linkcraft.fireMapChangeEvent(toTp);
                    MessageUtil.log(toTp.getName() + " warped to spawn from: " + Utils.getStringFromLocation(toTp.getLocation()), true);
                    Linkcraft.consoleCommand("essentialsspawn:spawn " + toTp.getName());
                }
            } else {
                Linkcraft.fireMapChangeEvent(playerSender);
                MessageUtil.log(playerSender.getName() + " warped to spawn from: " + Utils.getStringFromLocation(playerSender.getLocation()), true);
                Linkcraft.consoleCommand("essentialsspawn:spawn " + playerSender.getName());
            }
        }
    }
}
