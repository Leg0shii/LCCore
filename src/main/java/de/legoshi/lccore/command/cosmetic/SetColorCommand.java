package de.legoshi.lccore.command.cosmetic;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.manager.CosmeticManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Register
@Command(names = "setcolor")
public class SetColorCommand implements CommandClass {

    @Inject
    CosmeticManager cosmeticManager;

    @Command(names = "")
    public void setColor(CommandSender sender, String cosmeticType, String color) {

        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
        });

        Player player = (Player) sender;

        cosmeticManager.setColor(player, CosmeticType.valueOf(cosmeticType.toUpperCase()), color);

    }

}
