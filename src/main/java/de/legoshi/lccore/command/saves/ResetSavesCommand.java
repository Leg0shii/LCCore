package de.legoshi.lccore.command.saves;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ResetSavesCommand implements CommandExecutor {

    private final Linkcraft plugin;

    public ResetSavesCommand(Linkcraft plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ps.resetsaves")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission!"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(Utils.chat("&cInvalid syntax!"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        ConfigAccessor playerData = new ConfigAccessor(this.plugin, this.plugin.getPlayerdataFolder(), target.getUniqueId() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        if (playerDataConfig.getString("Saves").equals("{}")) {
            sender.sendMessage(Utils.chat("&cPlayer has no save data!"));
            return true;
        }

        sender.sendMessage(Utils.chat("&cPlayers saves are cleared!"));
        playerDataConfig.set("Saves", "{}");
        playerData.saveConfig();
        return true;
    }
}