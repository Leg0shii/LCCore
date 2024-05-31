package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.maps.MapsMenu;
import de.legoshi.lccore.menu.settings.SettingsMenu;
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
@Command(names = {"settings"}, permission = "settings", desc = "")
public class SettingsCommand implements CommandClass {

    @Inject private Injector injector;

    @Command(names = "")
    public void settings(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }


            Player player = (Player)sender;
            injector.getInstance(SettingsMenu.class).openGui(player, null);
        });
    }
}
