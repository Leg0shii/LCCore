package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.EssentialsManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"warp", "join"}, permission = "warp", desc = "<warp> [player]")
public class WarpCommand implements CommandClass {

    @Inject private EssentialsManager essentialsManager;

    @Command(names = "")
    public void warp(CommandSender sender, @ReflectiveTabComplete(clazz = EssentialsManager.class, method = "getWarpsFor", player = true) String warpName, @OptArg String playerName, @OptArg Boolean skipMapChange) {

//        if(warpName == null) {
//            if(sender instanceof Player) {
//                Player p = (Player)sender;
//                Bukkit.dispatchCommand()
//            }
//            return;
//        }

        if(!essentialsManager.isValidWarp(warpName)) {
            MessageUtil.send(Message.INVALID_WARP, sender);
            return;
        }

        if(sender instanceof ConsoleCommandSender) {
            if(playerName == null) {
                return;
            }

            Player toTp = Bukkit.getPlayer(playerName);

            if(toTp == null) {
                return;
            }

            if(skipMapChange == null || !skipMapChange) {
                Linkcraft.fireMapChangeEvent(toTp);
            }
            Linkcraft.consoleCommand("essentials:warp " + warpName + " " + playerName);
            return;
        }

        if(sender instanceof Player) {
            Player playerSender = (Player)sender;
            if(!essentialsManager.canWarpTo(playerSender, warpName)) {
                MessageUtil.send(Message.NO_PERM_WARP, playerSender);
                return;
            }

            if(playerName != null && MessageUtil.hasPerm(playerSender, "linkcraft.warp.others")) {
                Player toTp = Bukkit.getPlayer(playerName);
                if(toTp != null) {
                    if(skipMapChange == null || !skipMapChange) {
                        Linkcraft.fireMapChangeEvent(toTp);
                    }
                    Linkcraft.consoleCommand("essentials:warp " + warpName + " " + toTp.getName());
                } else {
                    MessageUtil.send(Message.IS_OFFLINE, playerSender, playerName);
                }
            } else {
                Linkcraft.fireMapChangeEvent(playerSender);
                Linkcraft.consoleCommand("essentials:warp " + warpName + " " + playerSender.getName());
            }
        }
    }
}
