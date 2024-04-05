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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.regex.Pattern;

@Register
@Command(names = {"nick"}, permission = "nick", desc = "<nickname|off> [player]")
public class NickCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private ChatManager chatManager;

    @Command(names = "")
    public void nick(CommandSender sender, @TabComplete(suggestions = "off") String nick, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) @OptArg String toNick) {

        Player toNickPlayer = toNick != null ? playerManager.playerByName(toNick) : null;
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
                return;
            }

            Player player = (Player)sender;

            boolean unsafeAll = player.hasPermission("linkcraft.nick.unsafe");
            boolean isSelf = toNickPlayer == null || toNickPlayer.getUniqueId().toString().equals(player.getUniqueId().toString());
            boolean unsafeSelf = isSelf && player.hasPermission("linkcraft.nick.unsafe.self");

            if(!checkNickFormat(nick) && !(unsafeAll || unsafeSelf)) {
                MessageUtil.send(Message.INVALID_NICKNAME_FORMAT, player);
                return;
            }

            if(!checkNickLength(nick) && (unsafeAll || unsafeSelf)) {
                MessageUtil.send(Message.INVALID_NICKNAME_FORMAT, player);
                return;
            }

            if(!checkNickTaken(nick, player) && !(unsafeAll || unsafeSelf)) {
                MessageUtil.send(Message.INVALID_NICKNAME_ALREADY_TAKEN, player);
                return;
            } else if(!checkNickTaken(nick, player) && (unsafeAll || unsafeSelf)) {
                MessageUtil.send(Message.INVALID_NICKNAME_ALREADY_TAKEN_WARN, player);
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

    private boolean checkNickFormat(String nick) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]*$");
        return pattern.matcher(nick).matches();
    }

    private boolean checkNickLength(String nick) {
        return nick.length() <= 16;
    }

    private boolean checkNickTaken(String nick, Player player) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(nick);

        return (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) && !playerManager.getNicksLower(player).contains(nick.toLowerCase());
    }
}
