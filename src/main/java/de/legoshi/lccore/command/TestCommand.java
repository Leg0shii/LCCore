package de.legoshi.lccore.command;

import de.legoshi.lccore.util.CommonUtil;
import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.command.CommandSender;
@Register
@Command(names = {"test"}, permission = "test", desc = "")
public class TestCommand implements CommandClass {

    @Command(names = "")
    public void test(CommandSender sender, ArgumentStack as) {
        sender.sendMessage(GUIUtil.colorize(Utils.joinArguments(as)));
    }
}
