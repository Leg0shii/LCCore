package de.legoshi.lccore.command;

import com.darkender.SecondPrefix.SecondPrefix;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelfMessageCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        String message = "";
        String tag = DeluxeTag.getPlayerDisplayTag(p);
        String star = SecondPrefix.getPrefix(p);

        if(!tag.isEmpty()) {
            message += tag + " ";
        }

        if(!star.isEmpty()) {
            message += star;
        }

        message += p.getDisplayName() + " &7» ";
        p.sendMessage("* " + Utils.chat(message) + Utils.chat(p, String.join(" ", args)));

        return true;
    }
}
