package de.legoshi.lccore.command.debug;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.TabComplete;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"test5"}, permission = "test5", desc = "")
public class Test5Command implements CommandClass {

    @Inject private PracticeManager practiceManager;

    @Command(names = "")
    public void test5(CommandSender sender, @TabComplete(suggestions = {"default", "hpk", "pku"}) String globalPracticeItem) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            practiceManager.setGlobalPracticeItem(globalPracticeItem);
            MessageUtil.send("Updated global practice item to: " + globalPracticeItem, sender);
        });
    }
}
