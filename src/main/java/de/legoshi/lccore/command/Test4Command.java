package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.listener.FAWEManager;
import de.legoshi.lccore.util.CommandException;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Register
@Command(names = {"test4"}, permission = "test4", desc = "")
public class Test4Command implements CommandClass {

    @Inject private FAWEManager faweManager;

    @Command(names = "")
    public void test4(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player) sender;
            try {
                List<Location> selection = faweManager.getSelection(player);
                player.sendMessage(Utils.getLocationDisplay(selection.get(0)));
                player.sendMessage(Utils.getLocationDisplay(selection.get(1)));
            } catch (CommandException e) {
                MessageUtil.send(e.getMessage(), player);
            }

        });
    }
}
