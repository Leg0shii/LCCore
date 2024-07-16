package de.legoshi.lccore.command.chat.display;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.display.StarDTO;
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

@Register
@Command(names = {"setstar"}, permission = "setstar", desc = "<player> <star>")
public class SetStarCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void setStar(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) String player, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getStars") @OptArg String star) {
        Player toSetStar = playerManager.playerByName(player);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if(toSetStar == null) {
                MessageUtil.send(Message.IS_OFFLINE, sender, player);
                return;
            }

            if(star == null) {
                playerManager.unequipStar(toSetStar);
                MessageUtil.send(Message.UNSET_STAR, toSetStar);
                playerManager.updatePlayer(toSetStar);
                return;
            }

            StarDTO starDTO = ConfigManager.starDisplay.get(star);
            if(starDTO == null) {
                MessageUtil.send(Message.STAR_DOES_NOT_EXIST, sender, star);
                return;
            }

            playerManager.equipStar(toSetStar, star);
            MessageUtil.send(Message.SET_STAR, toSetStar, starDTO.getDisplay());
            playerManager.updatePlayer(toSetStar);
        });
    }
}
