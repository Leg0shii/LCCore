package de.legoshi.lccore.command;

import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.LCSound;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Register
@Command(names = {"test2"}, permission = "test2", desc = "")
public class Test2Command implements CommandClass {

    @Command(names = "")
    public void test2(CommandSender sender, String sound, float volume, float pitch) {
        if(!(sender instanceof Player)) {
            return;
        }

        Player player = (Player)sender;

        player.playSound(player.getEyeLocation(), Sound.valueOf(sound), volume, pitch);
    }
}
