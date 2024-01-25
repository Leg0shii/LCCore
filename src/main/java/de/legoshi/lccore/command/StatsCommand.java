package de.legoshi.lccore.command;

import com.darkender.SecondPrefix.Prefix;
import com.darkender.SecondPrefix.SecondPrefix;
import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.Utils;
import me.clip.deluxetags.DeluxeTag;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            String hours = Utils.hoursToHoursAndMinutes(player.getStatistic(Statistic.PLAY_ONE_TICK) / 72000.0F);
            int jumps = player.getStatistic(Statistic.JUMP);
            String pp = (int)Linkcraft.getInstance().getEconomy().getBalance(player) + "pp";
            int tags = DeluxeTag.getAvailableTagIdentifiers(player).size();
            String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(player.getFirstPlayed()));
            String bonus = SecondPrefix.getPrefix(player);
            if(bonus.isEmpty()) {
                bonus = "&e#1";
            }
            sender.sendMessage(Utils.chat("&7Your LinkCraft Stats:\n&8&7Rank: " + prefix + "\n&8&7Bonus: &b" + bonus + "\n&8&7Time Played: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                this.executor.execute(() -> {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    if (offlinePlayer.getFirstPlayed() == 0L) {
                        sender.sendMessage(Utils.chat("&cNo player stats found for '" + args[0] + "'"));
                        return;
                    }
                    ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getInstance(), Linkcraft.getInstance().getPlayerdataFolder(), offlinePlayer.getUniqueId() + ".yml");
                    FileConfiguration playerDataConfig = playerData.getConfig();
                    String prefix = Linkcraft.getInstance().getChat().getPlayerPrefix(Utils.getLocationFromString(playerDataConfig.getString("lastlocation")).getWorld().getName(), offlinePlayer);

                    CompletableFuture<User> userFuture = Linkcraft.getInstance().luckPerms.getUserManager().loadUser(offlinePlayer.getUniqueId());
                    userFuture.thenAcceptAsync(user -> {
                        try {
                            String bonus = "";
                            SecondPrefix secondPrefix = (SecondPrefix)Bukkit.getPluginManager().getPlugin("SecondPrefix");
                            Class<?> clazz = secondPrefix.getClass();
                            Field prefixes = clazz.getDeclaredField("prefixes");
                            prefixes.setAccessible(true);
                            List<Prefix> prefixesList = (List<Prefix>)prefixes.get(secondPrefix);

                            for (Prefix sPrefix : prefixesList) {
                                Class<?> prefixClass = sPrefix.getClass();
                                Field permission = prefixClass.getDeclaredField("permission");
                                Field prefixField = prefixClass.getDeclaredField("prefix");
                                permission.setAccessible(true);
                                prefixField.setAccessible(true);
                                String perm = (String) permission.get(sPrefix);
                                String sPrefixPrefix = (String) prefixField.get(sPrefix);

                                if (user.getCachedData().getPermissionData().checkPermission(perm).asBoolean()) {
                                        bonus = sPrefixPrefix;
                                        break;
                                }
                            }

                            if(bonus.isEmpty()) {
                                bonus = "&e#1";
                            }

                            String hours = Utils.hoursToHoursAndMinutes(Utils.getStatistic(offlinePlayer, Constants.STAT_PLAY_ONE_TICK) / 72000.0F);
                            if (playerDataConfig.get("jumps") == null) {
                                playerDataConfig.set("jumps", Integer.valueOf(-1));
                                playerDataConfig.set("tags", Integer.valueOf(-1));
                                playerData.saveConfig();
                            }
                            String jumps = (playerDataConfig.getInt("jumps") != -1) ? String.valueOf(playerDataConfig.getInt("jumps")) : "No jumps recorded yet...";
                            String tags = (playerDataConfig.getInt("tags") != -1) ? "" + playerDataConfig.getStringList("tags").size() : "No tags recorded yet...";
                            String pp = (int)Linkcraft.getInstance().getEconomy().getBalance(offlinePlayer) + "pp";
                            String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(offlinePlayer.getFirstPlayed()));
                            sender.sendMessage(Utils.chat("&7" + args[0] + "'s LinkCraft Stats:\n&8&7Rank: " + prefix + "\n&8&7Bonus: &b" + bonus + "\n&8&7Time Played: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));

                        } catch (Exception ignored) {}
                    });
                });
            } else {
                String prefix = Linkcraft.getInstance().getChat().getPlayerPrefix(target);
                String hours =  Utils.hoursToHoursAndMinutes(target.getStatistic(Statistic.PLAY_ONE_TICK) / 72000.0F);
                String bonus = SecondPrefix.getPrefix(target);
                if(bonus.isEmpty()) {
                    bonus = "&e#1";
                }
                int jumps = target.getStatistic(Statistic.JUMP);
                String pp = (int)Linkcraft.getInstance().getEconomy().getBalance(target) + "pp";
                int tags = DeluxeTag.getAvailableTagIdentifiers(target).size();
                String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(target.getFirstPlayed()));
                sender.sendMessage(Utils.chat("&7" + target.getName() + "'s LinkCraft Stats:\n&8&7Rank: " + prefix + "\n&8&7Bonus: &b" + bonus + "\n&8&7Time Played: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
            }
        }
        return true;
    }
}