package de.legoshi.lccore.command.debug;

import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"test6"}, permission = "test6", desc = "")
public class Test6Command implements CommandClass {

    @Inject private PracticeManager practiceManager;

    @Command(names = "")
    public void test6(CommandSender sender, Integer position) {
        if(!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player player = (Player)sender;
        player.getInventory().setItem(position, new ItemStack(Material.REDSTONE, 1));
    }
}
