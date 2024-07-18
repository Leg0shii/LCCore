package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.PlayerCheckpoint;
import de.legoshi.lccore.manager.CheckpointManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"cptp"}, permission = "cptp", desc = "<player> [cp map]")
public class CheckpointTeleportCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private CheckpointManager checkpointManager;

    @Command(names = "")
    public void cpTp(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name,
                     @ReflectiveTabComplete(clazz = CheckpointManager.class, method = "getCpMaps") @OptArg String cpMap) {


        Player toTpTo = playerManager.playerByName(name);

        Linkcraft.async(() -> {
            if(cpMap != null && !checkpointManager.getCpMaps().contains(cpMap)) {
                MessageUtil.send(Message.CHECKPOINT_NO_MAP, sender, cpMap);
                return;
            }

            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            PlayerRecord record = playerManager.getPlayerRecord(toTpTo, name);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }

            Player player = (Player)sender;



            PlayerCheckpoint cp = cpMap != null ? checkpointManager.getPlayerCheckpointForMap(record.getUuid(), cpMap) : checkpointManager.getPlayerCheckpoint(record.getUuid());

            if(cp == null) {
                if(cpMap == null) {
                    MessageUtil.send(Message.CHECKPOINT_PLAYER_NO_CP, sender, record.getName());
                } else {
                    MessageUtil.send(Message.CHECKPOINT_PLAYER_NO_CP_FOR, sender, record.getName(), cpMap);
                }
                return;
            }

            Linkcraft.sync(() -> {
                player.teleport(cp.getLocation().toSpigot());
                MessageUtil.send(Message.CHECKPOINT_PLAYER_CP_TP, player, record.getName(), cp.getCheckpointMap());
            });

        });
    }
}
