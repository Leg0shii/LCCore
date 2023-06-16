package de.legoshi.lcpractice.command;

import java.util.Arrays;
import java.util.List;

import de.legoshi.lcpractice.LCPractice;
import de.legoshi.lcpractice.helper.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PracticeCommand implements CommandExecutor {

    private final LCPractice plugin;
    private final FileConfiguration playerdataConfig;

    public PracticeCommand(LCPractice plugin) {
        this.plugin = plugin;
        this.playerdataConfig = plugin.playerdataConfigAccessor.getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        FileConfiguration config = this.plugin.getConfig();
        Player p = (Player) sender;
        String uuid = p.getUniqueId().toString();
        if (this.playerdataConfig.get(uuid) != null) {
            String alreadyPracticingMessage = config.getString("already-practicing-message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyPracticingMessage));
            return true;
        }
        Double[] possibleYValues = {0.9375D, 0.875D, 0.75D, 0.625D, 0.5625D, 0.5D, 0.375D, 0.25D, 0.1875D, 0.125D, 0.0625D, 0.015625D, 0.0D};
        if (p.getVelocity().getY() == -0.0784000015258789D && Arrays.asList(possibleYValues).contains(p.getLocation().getY() % 1.0D)) {
            if (p.getInventory().firstEmpty() == -1) {
                String str = config.getString("full-inventory-message");
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
                return true;
            }
            String str1 = LocationHelper.getStringFromLocation(p.getLocation());
            this.playerdataConfig.set(uuid, str1);
            p.getInventory().addItem(this.plugin.getReturnItem());
            List<String> commandsToRun = config.getStringList("prac-run-commands");
            if (!commandsToRun.isEmpty()) {
                UnpracticeCommand.runCommands(p, commandsToRun);
            }
            String practiceMessage = config.getString("practice-message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', practiceMessage));
            return true;
        }
        String location = config.getString("block-message");
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', location));
        return true;
    }
}

