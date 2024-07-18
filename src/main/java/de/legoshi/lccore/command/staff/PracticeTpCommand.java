package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.models.PlayerCheckpoint;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"practp", "practicetp"}, permission = "practp", desc = "")
public class PracticeTpCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private PracticeManager practiceManager;

    @Command(names = "")
    public void pracTp(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toTpTo = playerManager.playerByName(name);

        Linkcraft.async(() -> {
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

            Location location = practiceManager.getPracticeLocation(record.getUuid());
            if(location == null) {
                MessageUtil.send(Message.NO_PRACTICE_LOCATION, player, record.getName());
                return;
            }

            Linkcraft.sync(() -> {
                player.teleport(location);
                MessageUtil.send(Message.PRACTICE_LOCATION_TP, player, record.getName());
            });
        });
    }
}
