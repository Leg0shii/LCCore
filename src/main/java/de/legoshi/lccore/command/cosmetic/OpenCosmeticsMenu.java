package de.legoshi.lccore.command.cosmetic;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.achievements.OpenAchievementProgressGUI;
import de.legoshi.lccore.menu.cosmetics.CosmeticsMenu;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.SubCommandClasses;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;


@Register
@Command(names = "cosmetics")
@SubCommandClasses({
        ChangeLeatherArmorColorCommand.class,
        SetColorCommand.class,
        UnlockCosmeticCommand.class
})
public class OpenCosmeticsMenu implements CommandClass {

    @Inject
    Injector injector;

    @Command(names = "")
    public void cosmeticsMenu(CommandSender sender) {

        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
        });

        Player player = (Player) sender;

        injector.getInstance(CosmeticsMenu.class).openGui(player);
    }
}
