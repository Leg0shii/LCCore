package de.legoshi.lccore.command.debug;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"test7"}, permission = "test7", desc = "")
public class Test7Command implements CommandClass {

    @Inject private PlayerManager playerManager;
    
    @Command(names = "")
    public void test7(CommandSender sender, String from, String to) {
        PlayerRecord fromRecord = playerManager.uuidByName(from);
        PlayerRecord toRecord = playerManager.uuidByName(to);

        if(fromRecord == null|| toRecord == null) {
            return;
        }

        try {
            playerManager.transferItems(fromRecord.getUuid(), toRecord.getUuid());
        } catch (Exception e) {
            MessageUtil.log("Error while attempting to transfer items", true);
        }
    }
}
