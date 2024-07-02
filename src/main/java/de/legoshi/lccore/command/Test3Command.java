package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.IdWrapper;
import de.legoshi.lccore.util.ItemUtil;
import de.legoshi.lccore.util.Register;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import team.unnamed.inject.Inject;

import java.util.List;
import java.util.Random;


@Register
@Command(names = {"test3"}, permission = "test3", desc = "<amtOrMin> <max> <velo>")
public class Test3Command implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void test3(CommandSender sender, @OptArg Integer amtOrMin, @OptArg Integer max, @OptArg Boolean velo) {
        if(!(sender instanceof Player)) {
            return;
        }

        if(amtOrMin == null) {
            amtOrMin = 64;
        }



        List<ItemStack> items = ItemUtil.getAllItemsAmt(64);
        Player finalResult = (Player)sender;

        IdWrapper idWrapper = new IdWrapper();
        Random random = new Random();

        Integer finalAmtOrMin = amtOrMin;
        int id = Linkcraft.syncRepeat(() -> {
            try {
                ItemStack item = items.remove(0);
                if(max != null) {
                    item.setAmount(random.nextInt((max - finalAmtOrMin) + 1) + finalAmtOrMin);
                } else {
                    item.setAmount(finalAmtOrMin);
                }
                Item res = finalResult.getWorld().dropItem(finalResult.getLocation(), item);

                if(velo) {
                    res.setVelocity(new Vector(2 * random.nextDouble() - 1, 2 * random.nextDouble() - 1, 2 * random.nextDouble() - 1));
                }
            } catch (IndexOutOfBoundsException e) {
                Linkcraft.cancelTask(idWrapper.id);
            }
        }, 1L, 20L);

        idWrapper.id = id;
    }
}
