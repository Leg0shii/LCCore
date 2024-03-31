package de.legoshi.lccore.command.punishment;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PunishmentManager;
import de.legoshi.lccore.player.PunishmentType;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"unmute"}, permission = "unmute", desc = "<name>")
public class UnmuteCommand implements CommandClass {

    @Inject private PunishmentManager punishmentManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void unmute(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String toUnmute) {

        Player player = playerManager.playerByName(toUnmute);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            String uuid = player != null ? player.getUniqueId().toString() : playerManager.uuidByName(toUnmute);
            if(uuid == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, toUnmute);
                return;
            }

            boolean unmuted = punishmentManager.removePunishment(uuid, PunishmentType.MUTE);
            boolean unFullMuted = punishmentManager.removePunishment(uuid, PunishmentType.FULL_MUTE);

            if(!unmuted && !unFullMuted) {
                MessageUtil.send(Message.PUNISH_WAS_NOT_PUNISHED, sender, toUnmute, "muted");
                return;
            }

            if(unmuted) {
                MessageUtil.send(Message.PUNISH_REMOVE_OTHER, sender, toUnmute, "muted");
                if(player != null && player.isOnline()) {
                    MessageUtil.send(Message.PUNISH_REMOVED, player, "unmuted");
                }
            }

            if(unFullMuted) {
                MessageUtil.send(Message.PUNISH_REMOVE_OTHER, sender, toUnmute, "full muted");
                if(player != null && player.isOnline()) {
                    MessageUtil.send(Message.PUNISH_REMOVED, player, "unmuted");
                }
            }
        });
    }
}
