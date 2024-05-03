package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@Register
@Command(names = {"lcrestart"}, permission = "lcrestart", desc = "")
public class LinkcraftRestartCommand implements CommandClass {

    @Command(names = "")
    public void lcRestart(CommandSender sender) {
        MessageUtil.broadcast(Message.SERVER_RESTART, false, "5");
        MessageUtil.log(Message.SERVER_RESTART, false, "5");
        Linkcraft.syncLater(() -> {
            MessageUtil.broadcast(Message.SERVER_RESTART, false, "3");
            MessageUtil.log(Message.SERVER_RESTART, false, "3");
            Linkcraft.syncLater(() -> {
                MessageUtil.broadcast(Message.SERVER_RESTART, false, "1");
                MessageUtil.log(Message.SERVER_RESTART, false, "1");
                Linkcraft.syncLater(Bukkit::shutdown, 20L * 60L);
            }, 20L * 60L * 2);
        }, 20L * 60L * 2);

    }
}
