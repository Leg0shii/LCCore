package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.listener.PreJoinListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import team.unnamed.inject.Inject;

public class LockdownCommand implements CommandExecutor {

    @Inject private PreJoinListener preJoinListener;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = Linkcraft.getInstance().lockdownConfig.getConfig();
        boolean state = config.getBoolean("lockdown-mode");
        config.set("lockdown-mode", !state);
        Linkcraft.getInstance().lockdownConfig.saveConfig();
        preJoinListener.reloadLockdownData();

        if(state) {
            sender.sendMessage(ChatColor.GREEN + "Server is no longer in lockdown mode!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Server is now in lockdown mode!");
        }

        return true;
    }
}
