package de.legoshi.lccore.command;

import de.legoshi.lccore.ui.ChatFormatGUI;
import de.legoshi.lccore.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// PARTLY REFACTORED
public class ChatFormatCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("lc.chat")) {
            sender.sendMessage(Utils.chat("&cInsufficient Permission!"));
            return true;
        }

        Player p = (Player) sender;
        p.openInventory((new ChatFormatGUI(p)).getInventory());
        return true;
    }

}