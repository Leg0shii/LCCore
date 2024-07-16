package de.legoshi.lccore.command.practice;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.player.practice.PracticeItem;
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
@Command(names = {"practice", "prac"}, permission = "practice", desc = "")
public class PracticeCommand implements CommandClass {

    @Inject private PracticeManager practiceManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void practice(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player player = (Player)sender;
        PracticeItem practiceItem = practiceManager.getPracticeItemFor(player);

        if(practiceManager.isInPractice(player)) {
            MessageUtil.sendMessageIfNotNull(player, practiceItem.getAlreadyPracticeMessage());
            MessageUtil.sendAudioIfNotNull(player, practiceItem.getAlreadyPracticeAudio());
            return;
        }

        if(playerManager.isFalling(player) || !playerManager.isOnGround(player)) {
            MessageUtil.sendMessageIfNotNull(player, practiceItem.getNotOnBlockMessage());
            MessageUtil.sendAudioIfNotNull(player, practiceItem.getNotInPracticeAudio());
            return;
        }

        if(playerManager.isInventoryFull(player)) {
            MessageUtil.sendMessageIfNotNull(player, practiceItem.getFullInventoryMessage());
            MessageUtil.sendAudioIfNotNull(player, practiceItem.getFullInventoryAudio());
            return;
        }

        Location pracLocation = player.getLocation();
        practiceManager.updatePracticeLocation(player, pracLocation);
        playerManager.giveItem(player, practiceItem.getItem());
        practiceManager.addToPracticeActionBarQueue(player);
        practiceManager.givePracticeGroup(player);

        MessageUtil.sendMessageIfNotNull(player, practiceItem.getPracticeMessage());
        MessageUtil.sendAudioIfNotNull(player, practiceItem.getPracticeAudio());

        // TODO: add a config toggle to let players modify this
        // Teleports player back to their practice location to prevent practice glitch
        Linkcraft.syncLater(() -> {
            if(player.isOnline()) {
                player.teleport(pracLocation);
            }
        }, 3L);
    }
}
