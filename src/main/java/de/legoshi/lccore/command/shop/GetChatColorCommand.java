package de.legoshi.lccore.command.shop;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.shop.ChatColorShopMenu;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Register
@Command(names = {"getchatcolor", "getchatcolor"}, permission = "getchatcolor", desc = "")
public class GetChatColorCommand implements CommandClass {

    @Inject private Injector injector;

    @Command(names = "")
    public void getChatColor(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }


            Player player = (Player)sender;
            injector.getInstance(ChatColorShopMenu.class).openGui(player, null);
        });
    }
}