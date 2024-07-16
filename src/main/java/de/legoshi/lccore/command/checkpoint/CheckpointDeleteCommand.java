package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.CheckpointManager;
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
@Command(names = {"cpd"}, permission = "cpd", desc = "<player>")
public class CheckpointDeleteCommand implements CommandClass {

    @Inject private CheckpointManager checkpointManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void deleteCp(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String playerName) {
        Player toUpdate = playerManager.playerByName(playerName);
        if(toUpdate == null) {
            MessageUtil.send(Message.IS_OFFLINE, sender, playerName);
            return;
        }

        checkpointManager.deletePlayerCheckpoint(toUpdate);
        checkpointManager.clearCurrentCheckpointMap(toUpdate);
        MessageUtil.send(Message.DELETED_CP, sender);
    }
}
