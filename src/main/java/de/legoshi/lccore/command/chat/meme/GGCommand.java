package de.legoshi.lccore.command.chat.meme;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"gg"}, permission = "gg", desc = "")
public class GGCommand implements CommandClass {

    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void gg(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            chatManager.sendMessageToCurrentChannel(player, Utils.chat(Linkcraft.getPlugin().getConfig().getString("ggmessage")));
        });
    }
}
