package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"lcpos1"}, permission = "lcpos", desc = "")
public class LCPos1Command implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void lcPos1(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            playerManager.setClipboardPos1(player, player.getLocation());
        });
    }
}
