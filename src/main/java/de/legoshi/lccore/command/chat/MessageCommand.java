package de.legoshi.lccore.command.chat;


import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PunishmentManager;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.player.PunishmentType;
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


@Register
@Command(names = {"msg", "message", "t", "tell", "w", "whisper"}, permission = "message", desc = "<player> <message>")
public class MessageCommand implements CommandClass {

    @Inject private ChatManager chatManager;
    @Inject private VisibilityManager visibilityManager;
    @Inject private PlayerManager playerManager;
    @Inject private PunishmentManager punishmentManager;

    @Command(names = "")
    public void message(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String toMessage,
                        @OptArg String message,  @OptArg ArgumentStack as) {
        Player recipient = playerManager.playerByName(toMessage);
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;

            if(punishmentManager.isPunished(player, PunishmentType.FULL_MUTE)) {
                punishmentManager.fullMuteMessage(player);
                return;
            }


            if(recipient == null || !recipient.isOnline() || (!visibilityManager.canSee(player, recipient)) && !player.hasPermission("linkcraft.message.vanished")) {
                MessageUtil.send(Message.IS_OFFLINE, player, toMessage);
                return;
            }

            if(message == null) {
                chatManager.openDirectChannel(player, recipient);
                return;
            }

            String result = Utils.joinArguments(message, as);

            chatManager.sendDirectChat(player, recipient, result);
        });
    }
}
