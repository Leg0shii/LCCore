package de.legoshi.lccore.command.misc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeowRaviCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && (sender.getName().equalsIgnoreCase("raviolimood") || sender.getName().equalsIgnoreCase("dkayee"))) {
            Player player = (Player) sender;

            // is no player being meowed at?
            if (args.length == 0) {
                player.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "You need to specify who you want to meow at!!");
                return true;
            }

            // is only 1 player being meowed at?
            if (args.length > 1) {
                player.sendMessage("You cant meow at more than 1 person sorry :(");
                return true;
            }


            //NOT USED RN
            // is the sender lonely?
//            if (sender.getName().equals(args[0])) {
//                player.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "You successfully meowed at" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " ...yourself?" + ChatColor.WHITE + ChatColor.BOLD + " do you not have anybody else");
//                return true;
//            }


            // is the target online?
            Player target = player.getServer().getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + args[0] + " is either not online or isn't real :(");
                return true;
            }

            // all message options
            String[] messages = {
                    ChatColor.WHITE + "" + ChatColor.BOLD + "You've got mail! " + "§9«§3«§f§lXIII§3»§9»" + " " + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.WHITE + ChatColor.BOLD + " delivered a friendly " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow" + ChatColor.WHITE + ChatColor.BOLD + "! :3",
                    ChatColor.WHITE + "" + ChatColor.BOLD + "Surprise! " + "§9«§3«§f§lXIII§3»§9»" + " " + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.WHITE + ChatColor.BOLD + " whispered a charming " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "in your ear. :3",
                    "§9«§3«§f§lXIII§3»§9»" + " " + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.WHITE + ChatColor.BOLD + " just couldn't resist sending you a virtual " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "! :3",
                    ChatColor.WHITE + "" + ChatColor.BOLD + "Guess what? " + "§9«§3«§f§lXIII§3»§9»" + " " + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " meowed " + ChatColor.WHITE + ChatColor.BOLD + "your way with a dash of adorableness! :3",
                    "§9«§3«§f§lXIII§3»§9»" + " " + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.WHITE + ChatColor.BOLD + " sent a sweet " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "meow " + ChatColor.WHITE + ChatColor.BOLD + "your way! :3",
                    "§9«§3«§f§lXIII§3»§9»" + " " + ChatColor.BLUE + ChatColor.BOLD + player.getName() + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + " meowed " + ChatColor.WHITE + ChatColor.BOLD + "at you! :3"
            };

            // meow :3
            int random = (int) (Math.random() * messages.length);
            target.sendMessage(messages[random]);
            player.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "You successfully meowed at " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + target.getName() + ChatColor.WHITE + ChatColor.BOLD + "!");
            return true;

        }
        else {
            sender.sendMessage(ChatColor.RED + "yeah you think you're that guy do you");
            return true; // Stop the command processing if the player is not raviolimood
        }
    }
}
