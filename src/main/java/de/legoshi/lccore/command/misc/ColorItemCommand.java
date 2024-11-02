package de.legoshi.lccore.command.misc;

import de.legoshi.lccore.manager.LuckPermsManager;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.stack.ArgumentStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import team.unnamed.inject.Inject;

@Register
@Command(names = "rename")
public class ColorItemCommand implements CommandClass {

    @Inject private LuckPermsManager luckPermsManager;

    @Command(names = "")
    public void rename(CommandSender sender, ArgumentStack names) {

        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player player = (Player) sender;

        if (!luckPermsManager.hasPermission(player, "linkcraft.colorItem")) {
            MessageUtil.send(Message.NO_PERMISSION, player);
            return;
        }

        ItemStack item = player.getItemInHand();

        if (item.getType().equals(Material.AIR)) {
            MessageUtil.send(Message.NO_ITEM_IN_HAND, player);
            return;
        }

        StringBuilder nameBuilder = new StringBuilder();
        while (names.hasNext()) {
            nameBuilder.append(names.next()).append(" ");
        }

        String newName = ChatColor.translateAlternateColorCodes('&', nameBuilder.toString().trim());

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(newName);
            item.setItemMeta(meta);

            MessageUtil.send("§aSuccessfully renamed your item to " + newName, player);
        }
    }
}




