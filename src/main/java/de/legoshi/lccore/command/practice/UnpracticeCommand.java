package de.legoshi.lccore.command.practice;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.player.practice.PracticeItem;
import de.legoshi.lccore.util.ItemUtil;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"unpractice", "unprac"}, permission = "unpractice", desc = "")
public class UnpracticeCommand implements CommandClass {

    @Inject private PracticeManager practiceManager;
    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void unpractice(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player player = (Player)sender;
        PracticeItem practiceItem = practiceManager.getPracticeItemFor(player);

        if(!practiceManager.isInPractice(player)) {
            MessageUtil.sendMessageIfNotNull(player, practiceItem.getNotInPracticeMessage());
            MessageUtil.sendAudioIfNotNull(player, practiceItem.getNotInPracticeAudio());
            return;
        }

        Location pracLocation = practiceManager.getPracticeLocation(player);
        practiceManager.removePracticeLocation(player);


        for(ItemStack item : player.getInventory().getContents()) {
            if(ItemUtil.hasNbtId(item, "practice")) {
                player.getInventory().removeItem(item);
            }
        }

        practiceManager.removeFromPracticeActionBarQueue(player);
        playerManager.clearActionBar(player);
        practiceManager.removePracticeGroup(player);
        player.teleport(pracLocation);
        MessageUtil.sendMessageIfNotNull(player, practiceItem.getUnpracticeMessage());
        MessageUtil.sendAudioIfNotNull(player, practiceItem.getUnpracticeAudio());


    }
}
