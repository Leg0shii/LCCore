package de.legoshi.lccore.command.chat.party;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PartyManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"pl"}, permission = "party.list", desc = "")
public class PLCommand implements CommandClass {

    @Inject
    private PartyManager partyManager;

    @Command(names = "")
    public void pl(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            partyManager.printPartyMemberListing(player);
        });
    }
}
