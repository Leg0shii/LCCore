package de.legoshi.lccore.command.chat;

import de.legoshi.lccore.manager.ChatManager;
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
@Command(names = {"sm", "selfmsg"}, permission = "selfmsg", desc = "[message]")
public class SelfMessageCommand implements CommandClass {

    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void selfMessage(CommandSender sender, @OptArg String first, @OptArg ArgumentStack as) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player p = (Player) sender;
        p.sendMessage("* " + chatManager.globalChat(p, Utils.joinArguments(first, as)));
    }
}
