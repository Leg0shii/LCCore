package de.legoshi.lccore.command;

import de.legoshi.lccore.util.Register;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
@Register
@Command(names = {"test"}, permission = "test", desc = "")
public class TestCommand implements CommandClass {

    @Command(names = "")
    public void test(CommandSender sender) {
        sender.sendMessage("Hello world!");
    }
}
