package de.legoshi.lcpractice.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.legoshi.lcpractice.LCPractice;
import de.legoshi.lcpractice.helper.LocationHelper;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnpracticeCommand implements CommandExecutor {

    private final LCPractice plugin;
    private final FileConfiguration playerdataConfig;
    private FileConfiguration config;

    public UnpracticeCommand(LCPractice plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.playerdataConfig = plugin.playerdataConfigAccessor.getConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        this.config = this.plugin.getConfig();
        Player p = (Player) sender;
        String uuid = p.getUniqueId().toString();
        String saved = this.playerdataConfig.getString(uuid);
        if (saved == null) {
            String notPracticingMessage = this.config.getString("not-practicing-message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', notPracticingMessage));
            return true;
        }
        Location loc = LocationHelper.getLocationFromString(saved);
        p.teleport(loc);
        this.playerdataConfig.set(uuid, null);
        ItemStack returnItem = this.plugin.getReturnItem();
        while (p.getInventory().containsAtLeast(returnItem, 1)) {
            p.getInventory().removeItem(new ItemStack[]{this.plugin.getReturnItem()});
        }
        List<String> commandsToRun = this.config.getStringList("unprac-run-commands");
        if (commandsToRun.isEmpty())
            return true;
        Map<String, String> values = new HashMap<>();
        values.put("player", p.getName());
        StrSubstitutor sub = new StrSubstitutor(values, "[", "]");
        for (int i = 0; i < commandsToRun.size(); i++) {
            String commandToRun = sub.replace(commandsToRun.toArray()[i]);
            Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), commandToRun);
        }
        String unpracticeMessage = this.config.getString("unpractice-message");
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', unpracticeMessage));
        return true;
    }
}

