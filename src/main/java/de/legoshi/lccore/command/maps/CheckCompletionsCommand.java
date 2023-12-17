package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.menu.MapsMenu;
import de.legoshi.lccore.menu.SideCoursesMenu;
import de.legoshi.lccore.menu.SideCoursesMenuOther;
import de.legoshi.lccore.util.Utils;
import fr.minuskube.inv.SmartInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckCompletionsCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.checkcompletions")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
            return true;
        }
        if (!(sender instanceof Player)) {
            return true;
        }

        if(args == null || args.length < 1) {
            sender.sendMessage(Utils.chat("&cUsage: /checkcompletions <player>"));
            return true;
        }

        Player player = (Player)sender;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

        if(offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "That player does not exist!");
            return true;
        }

        SideCoursesMenuOther temp = new SideCoursesMenuOther(offlinePlayer.getUniqueId().toString());
        SmartInventory inv = SmartInventory.builder()
                .id("main")
                .provider(temp)
                .size(6, 9)
                .title(Utils.chat("&l" + offlinePlayer.getName() + "'s Side Courses"))
                .manager(Linkcraft.getInstance().im)
                .build();
        temp.setInv(inv);
        inv.open(player);



        return true;
    }
}
