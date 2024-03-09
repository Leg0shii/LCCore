package de.legoshi.lccore.command.chat;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.command.flow.annotated.annotation.TabComplete;
import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.regex.Pattern;

@Register
@Command(names = {"nick"}, permission = "nick", desc = "<nickname|off> [player]`")
public class NickCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void nick(CommandSender sender, @TabComplete(suggestions = "off") String nick, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) @OptArg String toNick) {

        Player toNickPlayer = toNick != null ? Bukkit.getPlayer(toNick) : null;
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            boolean disable = nick.equalsIgnoreCase("off");

            if (!(sender instanceof Player)) {
                if(toNick != null) {
                    if(disable) {
                        playerManager.updateNick(toNickPlayer, "");
                        MessageUtil.send(Message.NICK_OFF, toNickPlayer);
                        MessageUtil.send(Message.NICK_CHANGE_OTHER, sender);
                    } else {
                        playerManager.updateNick(toNickPlayer, nick);
                        MessageUtil.send(Message.NICK_SET_TO, toNickPlayer, chatManager.nickPreview(toNickPlayer));
                        MessageUtil.send(Message.NICK_CHANGE_OTHER, sender);
                    }
                }

            }

            Player player = (Player)sender;

            if(!checkNick(nick) && !player.hasPermission("linkcraft.nick.unsafe")) {
                MessageUtil.send(Message.INVALID_NICKNAME, player);
                return;
            }



            if(toNick == null) {
                if(disable) {
                    playerManager.updateNick(player, "");
                    MessageUtil.send(Message.NICK_OFF, player);
                } else {
                    playerManager.updateNick(player, nick);
                    MessageUtil.send(Message.NICK_SET_TO, player, chatManager.nickPreview(player));
                }
                return;
            }

            if(!MessageUtil.hasPerm(player, "linkcraft.nick.other"))
                return;

            if(disable) {
                playerManager.updateNick(toNickPlayer, "");
                MessageUtil.send(Message.NICK_OFF, toNickPlayer);
                MessageUtil.send(Message.NICK_CHANGE_OTHER, player);
            } else {
                playerManager.updateNick(toNickPlayer, nick);
                MessageUtil.send(Message.NICK_SET_TO, toNickPlayer, chatManager.nickPreview(toNickPlayer));
                MessageUtil.send(Message.NICK_CHANGE_OTHER, player);
            }
        });
    }

    private boolean checkNick(String nick) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
        return pattern.matcher(nick).matches();
    }
}
