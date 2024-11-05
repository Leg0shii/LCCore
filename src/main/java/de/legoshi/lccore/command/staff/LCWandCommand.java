package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.util.GUIDescriptionBuilder;
import de.legoshi.lccore.util.ItemUtil;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

@Register
@Command(names = {"lcwand"}, permission = "lcwand", desc = "")
public class LCWandCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void lcWand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtil.send(Message.NOT_A_PLAYER, sender);
            return;
        }

        Player player = (Player)sender;


        if(playerManager.isInventoryFull(player)) {
            MessageUtil.send(Message.INVENTORY_FULL, player, "give LC Wand");
            return;
        }

        ItemStack lcWand = new ItemStack(Material.GOLD_AXE);
        lcWand = ItemUtil.setItemText(lcWand, new GUIDescriptionBuilder()
                .raw(ChatColor.YELLOW + "" + ChatColor.BOLD + "LC Wand")
                .build());
        lcWand = ItemUtil.addNbtId(lcWand, "lcwand");

        playerManager.giveItem(player, lcWand);
    }
}
