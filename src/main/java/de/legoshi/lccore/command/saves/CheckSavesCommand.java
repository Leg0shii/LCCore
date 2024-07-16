package de.legoshi.lccore.command.saves;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.ui.AdminUI;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// PARTLY REFACTORED
public class CheckSavesCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (!p.hasPermission("lc.admin")) {
            p.sendMessage(Utils.chat("&cInsufficient Permission!"));
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(Utils.chat("&cInvalid syntax!"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        Linkcraft.getPlugin().playerMap.put(p.getName(), target);
        p.openInventory(AdminUI.GUI(p, 1));
        p.sendMessage(Utils.chat("&aLooking at " + target.getName() + "'s saves!"));
        return true;
    }
}