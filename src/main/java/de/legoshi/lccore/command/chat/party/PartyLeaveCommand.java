package de.legoshi.lccore.command.chat.party;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PartyManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;


@Command(names = {"leave"}, permission = "party.leave", desc = "")
public class PartyLeaveCommand implements CommandClass {

    @Inject private PartyManager partyManager;

    @Command(names = "")
    public void leave(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            partyManager.removeFromParty(player);
        });
    }
}
