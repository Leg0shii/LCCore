package de.legoshi.lccore.command.chat;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.VisibilityManager;
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
@Command(names = {"ignore"}, permission = "ignore", desc = "<player>")
public class IgnoreCommand implements CommandClass {

    @Inject private ChatManager chatManager;
    @Inject private VisibilityManager visibilityManager;

    @Command(names = "")
    public void ignore(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) String playerName) {

        Player toIgnore = Bukkit.getPlayer(playerName);

        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;

            if(toIgnore == null || !visibilityManager.canSee(player, toIgnore)) {
                MessageUtil.send(Message.IS_OFFLINE, sender, playerName);
                return;
            }
            
            if(player.getUniqueId().toString().equals(toIgnore.getUniqueId().toString())) {
                MessageUtil.send(Message.IGNORE_SELF, sender);
                return;
            }

            chatManager.ignore(player, toIgnore);
        });
    }
}
