package de.legoshi.lccore.command.chat;


import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PunishmentManager;
import de.legoshi.lccore.player.PunishmentType;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"r", "reply"}, permission = "reply", desc = "[message]")
public class ReplyCommand implements CommandClass {

    @Inject private ChatManager chatManager;
    @Inject private PunishmentManager punishmentManager;

    @Command(names = "")
    public void reply(CommandSender sender, @OptArg String first, @OptArg ArgumentStack as) {
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

            if(first == null || first.isEmpty()) {
                chatManager.openDirectChannelWithLastMessaged(player);
                return;
            }

            chatManager.sendDirectToLastMessaged(player, Utils.joinArguments(first, as));
        });
    }
}
