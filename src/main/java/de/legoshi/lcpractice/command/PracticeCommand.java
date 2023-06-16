package de.legoshi.lcpractice.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.legoshi.lcpractice.LCPractice;
import de.legoshi.lcpractice.helper.LocationHelper;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PracticeCommand implements CommandExecutor {

    private final LCPractice plugin;
    private final FileConfiguration playerdataConfig;

    public PracticeCommand(LCPractice plugin) {
        this.plugin = plugin;
        this.playerdataConfig = plugin.playerdataConfigAccessor.getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        FileConfiguration config = this.plugin.getConfig();
        Player p = (Player) sender;
        String uuid = p.getUniqueId().toString();
        if (this.playerdataConfig.get(uuid) != null) {
            String alreadyPracticingMessage = config.getString("already-practicing-message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', alreadyPracticingMessage));
            return true;
        }
        Double[] possibleYValues = {Double.valueOf(0.9375D), Double.valueOf(0.875D), Double.valueOf(0.75D), Double.valueOf(0.625D), Double.valueOf(0.5625D), Double.valueOf(0.5D), Double.valueOf(0.375D), Double.valueOf(0.25D), Double.valueOf(0.1875D), Double.valueOf(0.125D), Double.valueOf(0.0625D), Double.valueOf(0.015625D), Double.valueOf(0.0D)};
        if (p.getVelocity().getY() == -0.0784000015258789D && Arrays.<Double>asList(possibleYValues).contains(Double.valueOf(p.getLocation().getY() % 1.0D))) {
            if (p.getInventory().firstEmpty() == -1) {
                String str = config.getString("full-inventory-message");
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
                return true;
            }
            String str1 = LocationHelper.getStringFromLocation(p.getLocation());
            this.playerdataConfig.set(uuid, str1);
            p.getInventory().addItem(new ItemStack[]{this.plugin.getReturnItem()});
            List<String> commandsToRun = config.getStringList("prac-run-commands");
            if (!commandsToRun.isEmpty()) {
                Map<String, String> values = new HashMap<>();
                values.put("player", p.getName());
                StrSubstitutor sub = new StrSubstitutor(values, "[", "]");
                for (int i = 0; i < commandsToRun.size(); i++) {
                    String commandToRun = sub.replace(commandsToRun.toArray()[i]);
                    Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), commandToRun);
                }
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

