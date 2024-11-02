package de.legoshi.lccore.command.achievements;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.AchievementManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

@Command(names = "unlock")
public class UnlockAchievementCommand implements CommandClass {

    @Inject private AchievementManager achievementManager;

    @Command(names = "")
    public void unlock(CommandSender sender, String achId) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
        });

        Player player = (Player) sender;

        achievementManager.giveAchievement(player, achievementManager.getAchievementById(achId));
        player.sendMessage("added the achievement: " + achId);
    }
}
