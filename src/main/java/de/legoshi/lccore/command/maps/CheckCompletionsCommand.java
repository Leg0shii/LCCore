package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.maps.MapsHolder;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.MapType;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Register
@Command(names = {"checkcompletions"}, permission = "checkcompletions", desc = "<player>")
public class CheckCompletionsCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private Injector injector;

    @Command(names = "")
    public void checkCompletions(CommandSender sender,
                                 @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String player) {

        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player victor = playerManager.playerByName(player);
        Linkcraft.async(() -> {
            PlayerRecord record = playerManager.getPlayerRecord(victor, player);
            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, player);
                return;
            }

            Player playerSender = (Player)sender;
            injector.getInstance(MapsHolder.class).openGui(playerSender, null, record, MapType.SIDE);
        });
    }
}
