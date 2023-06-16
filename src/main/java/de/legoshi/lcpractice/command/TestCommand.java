package de.legoshi.lcpractice.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) return true;
        sender.sendMessage("SIGNS: " + sender.hasPermission("pkcp.signs"));
        return false;
    }
}
