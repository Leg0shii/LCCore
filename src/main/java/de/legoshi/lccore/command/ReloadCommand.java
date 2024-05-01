package de.legoshi.lccore.command;

import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"lcreload"}, permission = "reload", desc = "")
public class ReloadCommand implements CommandClass {

    @Inject private ConfigManager configManager;

    @Command(names = "")
    public void reload(CommandSender sender) {
        configManager.loadConfigs(false);
        MessageUtil.send(Message.RELOAD_COMPLETE, sender);
    }
}
