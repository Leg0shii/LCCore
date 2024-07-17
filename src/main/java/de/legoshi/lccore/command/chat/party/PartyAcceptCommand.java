package de.legoshi.lccore.command.chat.party;


import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PartyManager;
import de.legoshi.lccore.manager.VisibilityManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = {"accept"}, permission = "party.accept", desc = "<name>")
public class PartyAcceptCommand implements CommandClass {

    @Inject private VisibilityManager visibilityManager;
    @Inject private PartyManager partyManager;

    @Command(names = "")
    public void accept(CommandSender sender, String inviterName) {
        Player inviter = Bukkit.getPlayer(inviterName);
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;

            if(inviter == null || !inviter.isOnline()) {
                MessageUtil.send(Message.IS_OFFLINE, player, inviterName);
                return;
            }

            partyManager.acceptInvite(player, inviter);
        });
    }
}
