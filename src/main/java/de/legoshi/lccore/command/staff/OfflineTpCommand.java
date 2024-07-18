package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.io.IOException;

@Register
@Command(names = {"otp"}, permission = "otp", desc = "<player>")
public class OfflineTpCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void otp(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toOtpTo = playerManager.playerByName(name);
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            PlayerRecord record = playerManager.getPlayerRecord(toOtpTo, name);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }

            try {
                Location location = playerManager.getOTPLocationNBT(record.getUuid());
                if(location == null) {
                    MessageUtil.send(Message.WORLD_DOES_NOT_EXIST, sender);
                    return;
                }

                Linkcraft.sync(() -> player.teleport(location));
            } catch (IOException ignored) {
                MessageUtil.send(Message.PLAYER_DATA_FILE_ERR, sender, record.getUuid());
            }
        });
    }
}
