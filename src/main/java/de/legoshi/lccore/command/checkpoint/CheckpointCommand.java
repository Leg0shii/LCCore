package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.database.models.PlayerCheckpoint;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"checkpoint", "cp"}, permission = "checkpoint", desc = "")
public class CheckpointCommand implements CommandClass {

    @Inject private CheckpointManager checkpointManager;

    @Command(names = "")
    public void checkpoint(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }
        Player player = (Player)sender;
        PlayerCheckpoint playerCheckpoint = checkpointManager.getPlayerCheckpoint(player);

        if(playerCheckpoint == null) {
            MessageUtil.send(Message.CHECKPOINT_NO_CP, player);
            return;
        }

        player.teleport(playerCheckpoint.getLocation().toSpigot());
    }
}
