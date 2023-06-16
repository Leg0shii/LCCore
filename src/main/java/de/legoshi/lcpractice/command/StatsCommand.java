package de.legoshi.lcpractice.command;

import de.legoshi.lcpractice.Linkcraft;
import de.legoshi.lcpractice.util.ConfigAccessor;
import de.legoshi.lcpractice.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StatsCommand implements CommandExecutor {

    private final ExecutorService executor;

    public StatsCommand() {
        this.executor = Executors.newCachedThreadPool();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lc.stats")) {
            sender.sendMessage(Utils.chat("&cInsufficient permission."));
            return true;
        }
        if (sender instanceof Player && (args.length == 0 || !sender.hasPermission("lc.stats.others"))) {
            Player player = (Player) sender;
            String prefix = Linkcraft.getInstance().getChat().getPlayerPrefix(player);
            float hours = player.getStatistic(Statistic.PLAY_ONE_TICK) / 72000.0F;
            int jumps = player.getStatistic(Statistic.JUMP);
            String pp = Linkcraft.getInstance().getEconomy().getBalance((OfflinePlayer) player) + "pp";
            int tags = DeluxeTag.getAvailableTagIdentifiers(player).size();
            String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(player.getFirstPlayed()));
            sender.sendMessage(Utils.chat("&7Your LinkCraft Stats:\n&8&7Rank: " + prefix + "\n&8&7Hours: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.executor.execute(() -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if (player.getFirstPlayed() == 0L) {
                        sender.sendMessage(Utils.chat("&cNo player stats found for '" + args[0] + "'"));
                        return;
                    }
                    ConfigAccessor playerData = new ConfigAccessor((JavaPlugin) Linkcraft.getInstance(), Linkcraft.getInstance().getPlayerdataFolder(), player.getUniqueId() + ".yml");
                    FileConfiguration playerDataConfig = playerData.getConfig();
                    String prefix = Linkcraft.getInstance().getChat().getPlayerPrefix(Utils.getLocationFromString(playerDataConfig.getString("lastlocation")).getWorld().getName(), player);
                    long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - player.getFirstPlayed());
                    if (playerDataConfig.get("jumps") == null) {
                        playerDataConfig.set("jumps", Integer.valueOf(-1));
                        playerDataConfig.set("tags", Integer.valueOf(-1));
                        playerData.saveConfig();
                    }
                    String jumps = (playerDataConfig.getInt("jumps") != -1) ? String.valueOf(playerDataConfig.getInt("jumps")) : "No jumps recorded yet...";
                    String tags = (playerDataConfig.getInt("tags") != -1) ? String.valueOf(playerDataConfig.getInt("tags")) : "No tags recorded yet...";
                    String pp = Linkcraft.getInstance().getEconomy().getBalance(player) + "pp";
                    String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(player.getFirstPlayed()));
                    sender.sendMessage(Utils.chat("&7" + args[0] + "'s LinkCraft Stats:\n&8&7Rank: " + prefix + "\n&8&7Hours: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
                });
            } else {
                String prefix = Linkcraft.getInstance().getChat().getPlayerPrefix(target);
                float hours = target.getStatistic(Statistic.PLAY_ONE_TICK) / 72000.0F;
                int jumps = target.getStatistic(Statistic.JUMP);
                String pp = Linkcraft.getInstance().getEconomy().getBalance((OfflinePlayer) target) + "pp";
                int tags = DeluxeTag.getAvailableTagIdentifiers(target).size();
                String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(target.getFirstPlayed()));
                sender.sendMessage(Utils.chat("&7" + target.getName() + "'s LinkCraft Stats:\n&8&7Rank: " + prefix + "\n&8&7Hours: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
            }
        }
        return true;
    }
}