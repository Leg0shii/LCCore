package de.legoshi.lccore.command.staff.punishment;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.Punishment;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PunishmentManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.player.PunishmentType;
import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Date;

@Register
@Command(names = {"mute"}, permission = "mute", desc = "<name> <length> [-s] [reason]")
public class MuteCommand implements CommandClass {

    @Inject private PunishmentManager punishmentManager;
    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void mute(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String toMute, String length, @OptArg ArgumentStack reasonList) {

        Player player = playerManager.playerByName(toMute);
        Linkcraft.async(() -> {
            PlayerRecord record = playerManager.getPlayerRecord(player, toMute);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, toMute);
                return;
            }

            String uuid = record.getUuid();
            String name = record.getName();

            String reason = Utils.joinArguments(reasonList);
            boolean silent = reason.contains("-s");

            if(silent) {
                reason = reason.replace("-s", "");
                reason = reason.trim();
            }

            if(reason.isEmpty()) {
                reason = "Misconduct";
            }

            Date until = punishmentManager.getDateFromLength(length);

            if(until == null) {
                MessageUtil.send(Message.PUNISH_WRONG_LENGTH, sender, length);
                return;
            }

            Punishment punishment = punishmentManager.addPunishment(uuid, PunishmentType.MUTE, until, reason);

            if(punishment != null) {
                MessageUtil.send(Message.PUNISH_ALREADY_PUNISHED, sender, name, "muted", GUIUtil.ISOStringWithTime(punishment.getLength()));
                return;
            }

            if(silent) {
                MessageUtil.broadcast(chatManager.getPlayersWithPerm("linkcraft.punish.silent"), Message.PUNISH_UNTIL_SILENT, true, name, "muted", GUIUtil.ISOStringWithTime(until), reason);
            } else {
                MessageUtil.broadcast(Message.PUNISH_UNTIL, true, name, "muted", GUIUtil.ISOStringWithTime(until), reason);
            }
        });
    }
}
