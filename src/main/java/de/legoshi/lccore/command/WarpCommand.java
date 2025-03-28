package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.EssentialsManager;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
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
    @Inject private MapManager mapManager;

    @Command(names = "")
    public void warp(CommandSender sender, @ReflectiveTabComplete(clazz = EssentialsManager.class, method = "getWarpsFor", player = true) String warpName, @OptArg String playerName, @OptArg String param) {
        if(!essentialsManager.isValidWarp(warpName)) {
            MessageUtil.send(Message.INVALID_WARP, sender);
            return;
        }

        boolean skipMapChange = param != null && param.equalsIgnoreCase("true");
        boolean bypass = param != null && param.equalsIgnoreCase("bypass");

        if(sender instanceof ConsoleCommandSender) {
            handleConsoleWarp(warpName, playerName, skipMapChange, bypass);
            return;
        }

        if (playerName == null) {
            handlePlayerWarp((Player) sender, warpName);
        } else {
            handleOtherPlayerWarp((Player) sender, warpName, playerName, skipMapChange, bypass);
        }
    }

    private void handleConsoleWarp(String warp, String playerName, boolean skipMapChange, boolean bypass) {
        if(playerName == null) return;
        Player player = Bukkit.getPlayer(playerName);
        if(player == null) return;
        warpPlayer(warp, player, skipMapChange, bypass);
    }

    private void handlePlayerWarp(Player player, String warp) {
        if (!essentialsManager.canWarpTo(player, warp)) {
            MessageUtil.send(Message.NO_PERM_WARP, player);
            return;
        }

        warpPlayer(warp, player);
    }

    private void handleOtherPlayerWarp(Player player, String warp, String playerName, boolean skipMapChange, boolean bypass) {
        Player toWarp = Bukkit.getPlayer(playerName);
        if(toWarp == null) {
            MessageUtil.send(Message.IS_OFFLINE, player, playerName);
            return;
        }

        warpPlayer(warp, toWarp, skipMapChange, bypass);
    }

    private void warpPlayer(String warp, Player player, boolean skipEvent, boolean bypass) {
        if(!bypass && !mapManager.canAccessWarp(player, warp)) {
            MessageUtil.send(Message.NO_PERM_WARP, player);
            return;
        }

        MessageUtil.log(player.getName() + " warped to " + warp + " from: " + Utils.getStringFromLocation(player.getLocation()), true);

        if(!skipEvent)
            Linkcraft.fireMapChangeEvent(player, mapManager.getMap(warp));

        Linkcraft.consoleCommand("essentials:warp " + warp + " " + player.getName());
    }

    private void warpPlayer(String warp, Player player) {
        warpPlayer(warp, player, false, false);
    }
}
