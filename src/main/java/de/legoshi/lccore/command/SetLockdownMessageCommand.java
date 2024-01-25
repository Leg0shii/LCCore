package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.listener.PreJoinListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import team.unnamed.inject.Inject;

public class SetLockdownMessageCommand implements CommandExecutor {

    @Inject private PreJoinListener preJoinListener;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args == null || args.length < 1) {
            sender.sendMessage(ChatColor.RED + "This command requires arguments");
            return true;
        }

        StringBuilder message = new StringBuilder();

        for(String arg : args) {
            message.append(arg).append(" ");
        }
        message.deleteCharAt(message.length() - 1);

        FileConfiguration config = Linkcraft.getInstance().lockdownConfig.getConfig();
        config.set("lockdown-message", message.toString());
        Linkcraft.getInstance().lockdownConfig.saveConfig();
        preJoinListener.reloadLockdownData();
        sender.sendMessage(ChatColor.GREEN + "Locked message has been set to: " + message);
        return true;
    }
}
