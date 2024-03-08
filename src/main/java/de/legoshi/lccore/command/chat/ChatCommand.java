package de.legoshi.lccore.command.chat;


import de.legoshi.lccore.command.flow.annotated.annotation.TabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.player.chat.ChatChannel;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"chat", "ch"}, permission = "chat", desc = "<all/staff/party>")
public class ChatCommand implements CommandClass {

    @Inject private ChatManager chatManager;

    @Command(names = "")
    public boolean chat(CommandSender sender, @TabComplete(suggestions = {"all", "staff", "party"}) String channelStr) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return true;
        }
        Player player = (Player)sender;
        ChatChannel channel = ChatChannel.byStr(channelStr);
        if(channel == null) {
            return false;
        }

        chatManager.setChannel(player, channel);

        return true;
    }
}
