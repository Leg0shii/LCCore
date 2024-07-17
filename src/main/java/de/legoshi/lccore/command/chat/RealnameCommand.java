package de.legoshi.lccore.command.chat;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.player.display.LCPlayer;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@Register
@Command(names = {"realname"}, permission = "realname", desc = "<nick>")
public class RealnameCommand implements CommandClass {

    @Inject private VisibilityManager visibilityManager;
    @Inject private ChatManager chatManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void realname(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getNicks", player = true) String toCheck) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player p = (Player)sender;

            Map<String, String> selected = new HashMap<>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!visibilityManager.canSee(p, player)) {
                    continue;
                }
                LCPlayer lcPlayer = playerManager.getPlayer(player);
                if(chatManager.removeChatColour(lcPlayer.getNick()).toLowerCase().contains(toCheck.toLowerCase()) || player.getName().toLowerCase().contains(toCheck.toLowerCase())) {
                    selected.put(chatManager.rankNickStar(player), player.getName());
                }

            }

            for(Map.Entry<String, String> entry : selected.entrySet()) {
                MessageUtil.send(Message.NICK_REALNAME, sender, entry.getKey(), entry.getValue());
            }
        });
    }
}
