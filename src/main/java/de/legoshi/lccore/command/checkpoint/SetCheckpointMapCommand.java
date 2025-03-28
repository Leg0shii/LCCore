package de.legoshi.lccore.command.checkpoint;

import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"setcpmap"}, permission = "setcpmap", desc = "<map> <player>")
public class SetCheckpointMapCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private CheckpointManager checkpointManager;
    @Inject private DBManager db;

    @Command(names = "")
    public void setCpMap(CommandSender sender, String map,
                         @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String playerName) {

        Player toUpdate = playerManager.playerByName(playerName);
        PlayerRecord record = playerManager.getPlayerRecord(toUpdate, playerName);

        if(record == null) {
            MessageUtil.send(Message.NEVER_JOINED, sender, playerName);
            return;
        }

        checkpointManager.setCheckpointMap(record.getUuid(), map);
        MessageUtil.send(Message.UPDATED_CP_MAP, sender, record.getName(), map);
    }
}
