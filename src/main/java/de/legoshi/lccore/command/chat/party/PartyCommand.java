package de.legoshi.lccore.command.chat.party;


import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PartyManager;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.ArgOrSub;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.SubCommandClasses;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;


@Register
@Command(names = {"party", "p"}, permission = "party", desc = "")
@SubCommandClasses({
    PartyAcceptCommand.class,
    PartyLeaveCommand.class,
    PartyInviteCommand.class,
    PartyKickCommand.class,
    PartyListCommand.class,
    PLCommand.class
})
@ArgOrSub(value = true)
public class PartyCommand implements CommandClass {

    @Inject
    private PartyManager partyManager;
    @Inject private VisibilityManager visibilityManager;

    @Command(names = "")
    public void party(CommandSender sender, String toInviteName) {

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
