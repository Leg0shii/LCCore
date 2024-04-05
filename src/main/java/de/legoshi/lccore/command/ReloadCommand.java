package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public ReloadCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!MessageUtil.hasPerm(sender, "lc.reload")) {
            return true;
        }
        plugin.configManager.loadConfigs(false);
        MessageUtil.send(Message.RELOAD_COMPLETE, sender);
        return true;
    }

}