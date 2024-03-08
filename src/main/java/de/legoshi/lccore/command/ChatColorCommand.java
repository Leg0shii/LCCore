package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.ui.ChatColorGUI;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

// PARTLY REFACTORED
@Register
@Command(names = {"chatcolor"})
public class ChatColorCommand implements CommandClass {

    @Inject private Injector injector;

    @Command(names = "")
    public void chatColor(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            if (!sender.hasPermission("lc.chat")) {
                sender.sendMessage(Utils.chat("&cInsufficient Permission!"));
                return;
            }

            Player player = (Player)sender;
            ChatColorGUI gui = new ChatColorGUI(player, 0, injector);
            player.openInventory(gui.getInventory());
        });
    }
}