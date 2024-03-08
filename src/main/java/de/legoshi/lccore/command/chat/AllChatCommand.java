package de.legoshi.lccore.command.chat;


import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;


@Register
@Command(names = {"ac", "allchat"}, permission = "allchat", desc = "<message>")
public class AllChatCommand implements CommandClass {

    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void allChat(CommandSender sender, String first, ArgumentStack as) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            chatManager.sendGlobalMessage(player, Utils.joinArguments(first, as));
        });
    }
}
