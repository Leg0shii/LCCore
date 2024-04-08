package de.legoshi.lccore.command.chat;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"muteac"}, permission = "muteac", desc = "")
public class MuteAllChatCommand implements CommandClass {

    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void muteAllChat(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            boolean muted = chatManager.isGlobalChatMuted();
            String msg = muted ? "unmuted" : "muted";
            chatManager.setGlobalChatMuted(!muted);
            MessageUtil.send(Message.GLOBAL_MUTE, sender, msg);
        });
    }
}
