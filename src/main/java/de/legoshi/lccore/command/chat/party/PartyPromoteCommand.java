package de.legoshi.lccore.command.chat.party;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PartyManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"promote"}, permission = "party.promote", desc = "")
public class PartyPromoteCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private PartyManager partyManager;

    @Command(names = "")
    public void promote(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String playerName) {
        Player toPromote = playerManager.playerByName(playerName);
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            PlayerRecord record = playerManager.getPlayerRecord(toPromote, playerName);

            if(record == null) {
                MessageUtil.send(Message.PARTY_PLAYER_NOT_IN, player);
                return;
            }

            partyManager.promote(player, record.getUuid());
        });
    }
}
