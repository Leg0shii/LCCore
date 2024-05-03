package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.io.IOException;

@Register
@Command(names = {"otphere"}, permission = "otphere", desc = "<player>")
public class OfflineTpHereCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void otpHere(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toOtpHereTo = playerManager.playerByName(name);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            PlayerRecord record = playerManager.getPlayerRecord(toOtpHereTo, name);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }

            Location curr = player.getLocation();
            try {
                playerManager.spigotToNBTLocation(curr, record.getUuid());
                player.sendMessage(ChatColor.GREEN + "Updated " + record.getName() + "'s location to: " + Utils.getStringFromLocation(curr));
            } catch (IOException e) {
                MessageUtil.send(Message.PLAYER_DATA_FILE_ERR, sender, record.getUuid());
            }
        });
    }
}
