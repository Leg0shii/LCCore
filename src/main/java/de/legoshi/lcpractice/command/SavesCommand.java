package de.legoshi.lcpractice.command;

import de.legoshi.lcpractice.ui.SavesUI;
import de.legoshi.lcpractice.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// PARTLY REFACTORED
public class SavesCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (!p.hasPermission("ps.save") || (p.hasPermission("lc.normal") && p.getWorld().getName().equals("bonus"))) {
            p.sendMessage(Utils.chat("&cYou don't have permission to do this command! (ps.saves)"));
            return true;
        }
        p.openInventory(SavesUI.GUI(p, 1));
        p.sendMessage(Utils.chat("&aLooking at your saves!"));
        return true;
    }
}