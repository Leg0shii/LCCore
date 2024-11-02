package de.legoshi.lccore.command.cosmetic;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.cosmetics.ColorCosmetic;
import de.legoshi.lccore.cosmetics.CosmeticType;
import de.legoshi.lccore.manager.CosmeticManager;
import de.legoshi.lccore.menu.cosmetics.CosmeticsColorMenu;
import de.legoshi.lccore.menu.cosmetics.CosmeticsMenu;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Register
@Command(names = "color")
public class ChangeLeatherArmorColorCommand implements CommandClass {

    @Inject private CosmeticManager cosmeticManager;
    @Inject Injector injector;

    @Command(names = "")
    public void changeColor(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
        });

        Player player = (Player) sender;

        injector.getInstance(CosmeticsColorMenu.class).openGui(player, CosmeticType.BOOTS);
    }
}
