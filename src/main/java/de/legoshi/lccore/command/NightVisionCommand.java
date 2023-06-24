package de.legoshi.lccore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if(args != null && args.length > 0 && args[0].equalsIgnoreCase("clear")) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        } else {

            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
        }
        return true;
    }
}
