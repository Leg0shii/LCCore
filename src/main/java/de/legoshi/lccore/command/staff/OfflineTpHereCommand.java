package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"otphere"}, permission = "otphere", desc = "<player>")
public class OfflineTpHereCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void otpHere(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Player toOtpHereTo = playerManager.playerByName(name);
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }



            Player player = (Player)sender;

            if (toOtpHereTo != null) {
                player.sendMessage(ChatColor.RED + player.getName() + " is currently online!");
                return;
            }

            PlayerRecord record = playerManager.getPlayerRecord(null, name);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, name);
                return;
            }

            FileConfiguration pracConfig = Linkcraft.getPlugin().playerConfig.getConfig();
            String saved = pracConfig.getString(record.getUuid());

            if(saved != null) {
                pracConfig.set(record.getUuid(), null);
                player.sendMessage(ChatColor.RED + record.getName() + " was in practice, automatically unpracticed from: " + saved);
            }

            Location curr = player.getLocation();
            playerManager.saveOTPLocation(curr, record.getUuid(), player);
            player.sendMessage(ChatColor.GREEN + "Updated " + record.getName() + "'s location to: " + Utils.getStringFromLocation(curr));
        });
    }
}
