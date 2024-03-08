package de.legoshi.lccore.command.format;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.settings.NameFormatMenu;
import de.legoshi.lccore.util.ColorHelper;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Register
@Command(names = {"underline"}, permission = "underline", desc = "")
public class UnderlineCommand implements CommandClass {

    @Inject private Injector injector;

    @Command(names = "")
    public void underline(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player)sender;
            injector.getInstance(NameFormatMenu.class).openGui(player, null, ColorHelper.ChatFormat.UNDERLINE, new ItemStack(Material.GOLD_ORE), "Underline your name");
        });
    }
}
