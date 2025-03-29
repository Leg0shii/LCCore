package de.legoshi.lccore.command.practice;

import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"unpractice", "unprac"}, permission = "unpractice", desc = "")
public class UnpracticeCommand implements CommandClass {

    @Inject private PracticeManager practiceManager;

    @Command(names = "")
    public void unpractice(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        practiceManager.unpractice((Player)sender);
    }
}
