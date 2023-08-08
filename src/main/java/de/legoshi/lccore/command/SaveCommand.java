package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.ui.SaveCreationUI;
import de.legoshi.lccore.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// PARTLY REFACTORED
public class SaveCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public SaveCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("ps.save") || (p.hasPermission("lc.normal") && p.getWorld().getName().equals("bonus")) || (p.hasPermission("lc.checkpoint") && p.getWorld().getName().equals("checkpoint"))) {
                p.sendMessage(Utils.chat("&cYou don't have permission to do this command! (ps.save)"));
                return true;
            }

            if (p.getLocation().getY() % 1.0D == 0.0D) {
                if (p.getLocation().add(0.3D, 0.0D, 0.0D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(-0.3D, 0.0D, 0.0D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(0.0D, 0.0D, 0.3D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(0.0D, 0.0D, -0.3D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(0.3D, 0.0D, 0.3D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(0.3D, 0.0D, -0.3D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(-0.3D, 0.0D, -0.3D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }

                if (p.getLocation().add(-0.3D, 0.0D, 0.3D).getBlock().getType().isSolid()) {
                    p.sendMessage(Utils.chat("&cYou can't save in a block!"));
                    return true;
                }
            }

            if (!p.isOnGround()) {
                p.sendMessage(Utils.chat("&cYou must be on the ground!"));
                return true;
            }

            String uuid = p.getUniqueId().toString();
            String saveName = String.join(" ", args);

            if (args.length < 1) {
                p.sendMessage(Utils.chat("&a/save <name>"));
                return true;
            }

            this.plugin.saveCreations.put(uuid, saveName);
            p.openInventory(SaveCreationUI.GUI(p));
        }
        return true;
    }
}
