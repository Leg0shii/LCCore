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
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;


@Register
@Command(names = {"sc", "staffchat", "helpop"}, permission = "staffchat", desc = "<message>")
public class StaffChatCommand implements CommandClass {

    @Inject private ChatManager chatManager;
    @Inject private PunishmentManager punishmentManager;

    @Command(names = "")
    public void staffChat(CommandSender sender, String first, ArgumentStack as) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;

            if(punishmentManager.isPunished(player, PunishmentType.FULL_MUTE)) {
                MessageUtil.send(Message.PUNISH_YOU_ARE_FULL_MUTED, player);
                return;
            }

            chatManager.sendStaffMessage(player, Utils.joinArguments(first, as));
        });
    }
}
