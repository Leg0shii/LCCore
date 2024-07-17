package de.legoshi.lccore.command.chat.party;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PartyManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;


@Command(names = {"kick"}, permission = "party.kick", desc = "<player>")
public class PartyKickCommand implements CommandClass {

    @Inject private PartyManager partyManager;
    @Inject private VisibilityManager visibilityManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void kick(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) String playerName) {
        Player toBeKicked = Bukkit.getPlayer(playerName);
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;


            String uuid = "";
            Player o = Bukkit.getPlayer(playerName);
            if(o != null) {
                uuid = o.getUniqueId().toString();
            } else {
                OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
                uuid = op.getUniqueId().toString();
            }

            if(!uuid.isEmpty()) {
                partyManager.kickFromParty(player, uuid);
            }
        });
    }
}
