package de.legoshi.lccore.command.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.achievements.AchievementProgressionMenu;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Command(names = {"progress"})
public class OpenAchievementProgressGUI implements CommandClass {

    @Inject
    private Injector injector;

    @Command(names = "")
    public void progress(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String name) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }

            Player player = (Player) sender;

            Player target = player;

            if (name != null && Bukkit.getPlayer(name) != null) {
                target = Bukkit.getPlayer(name);
            }


            injector.getInstance(AchievementProgressionMenu.class).openGui(player, target, null);
        });
    }
}
