package de.legoshi.lccore.command.debug;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.IdWrapper;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

@Register
@Command(names = {"test4"}, permission = "test4", desc = "")
public class Test4Command implements CommandClass {

    @Command(names = "")
    public void test4(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;

            Block b = player.getTargetBlock((HashSet<Byte>)null, 5);

            if(b != null && (b.getType().equals(Material.RAILS) || b.getType().equals(Material.POWERED_RAIL))) {
                Linkcraft.sync(() -> {
                    IdWrapper idWrapper = new IdWrapper();
                    AtomicInteger i = new AtomicInteger();
                    idWrapper.id = Linkcraft.syncRepeat(() -> {
                        if(i.get() == 1000) {
                            Linkcraft.cancelTask(idWrapper.id);
                        }
                        player.getWorld().spawnEntity(b.getLocation().add(0.5, 2, 0.5), EntityType.MINECART);
                        i.getAndIncrement();
                    }, 2L, 0L);

                });

            }
        });
    }
}
