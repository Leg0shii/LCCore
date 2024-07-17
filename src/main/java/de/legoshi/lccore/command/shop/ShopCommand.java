package de.legoshi.lccore.command.shop;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.shop.ShopMenu;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Register
@Command(names = {"shop", "get", "buy"}, permission = "shop", desc = "")
public class ShopCommand implements CommandClass {

    @Inject private Injector injector;

    @Command(names = "")
    public void shop(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }


            Player player = (Player)sender;
            injector.getInstance(ShopMenu.class).openGui(player, null);
        });
    }
}
