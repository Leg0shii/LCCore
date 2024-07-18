package de.legoshi.lccore.command.chat;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.List;

@Register
@Command(names = {"ignore"}, permission = "ignore", desc = "[player]")
public class IgnoreCommand implements CommandClass {

    @Inject private ChatManager chatManager;
    @Inject private VisibilityManager visibilityManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void ignore(CommandSender sender, @OptArg @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String playerName) {
        Player toIgnore;
        if(playerName != null) {
            toIgnore = playerManager.playerByName(playerName);
        } else {
            toIgnore = null;
        }

        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;

            if(playerName == null) {
                List<String> ignoredIds = chatManager.getIgnores(player);
                if(ignoredIds == null || ignoredIds.isEmpty()) {
                    MessageUtil.send(Message.IGNORE_LIST_NONE, player);
                    return;
                }
                StringBuilder sb = new StringBuilder();
                int ignoredIdsSize = ignoredIds.size();
                for (int i = 0; i < ignoredIdsSize; i++) {
                    String id = ignoredIds.get(i);
                    sb.append(playerManager.nameByUUID(id));
                    if (i < ignoredIdsSize - 1) {
                        sb.append(", ");
                    }
                }
                MessageUtil.send(Message.IGNORE_LIST, player, sb.toString());
                return;
            }

            PlayerRecord record = playerManager.getPlayerRecord(toIgnore, playerName);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, player, playerName);
                return;
            }

            String uuid = record.getUuid();

            if(player.getUniqueId().toString().equals(uuid)) {
                MessageUtil.send(Message.IGNORE_SELF, sender);
                return;
            }

            if(chatManager.getIgnores(player).size() >= 200) {
                MessageUtil.send(Message.IGNORE_TOO_MANY, player);
                return;
            }

            chatManager.ignore(player, uuid);
        });
    }
}
