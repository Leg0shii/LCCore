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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"invite"}, permission = "party.invite", desc = "<player>")
public class PartyInviteCommand implements CommandClass {

    @Inject private PartyManager partyManager;
    @Inject
    private VisibilityManager visibilityManager;

    @Command(names = "")
    public void invite(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPlayers", player = true) String toInviteName) {
        Player invitee = Bukkit.getPlayer(toInviteName);
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;

            if(invitee == null || !invitee.isOnline() || !visibilityManager.canSee(player, invitee)) {
                MessageUtil.send(Message.IS_OFFLINE, player, toInviteName);
                return;
            }

            partyManager.inviteToParty(player, invitee);
        });
    }
}
