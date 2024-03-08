package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.player.display.LCPlayer;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.Constants;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Date;

@Register
@Command(names = {"stats", "stat", "statistic", "statistics"}, permission = "stats", desc = "")
public class StatsCommand implements CommandClass {

    @Inject private PlayerManager playerManager;

    @Command(names = "")
    public void stats(CommandSender sender, @OptArg String name) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }



            // self stats
            if(name == null || name.isEmpty()) {

                Player player = (Player)sender;
                FileConfiguration playerDataConfig = getFileConfiguration(player);

                LCPlayer lcPlayer = playerManager.getPlayer(player);
                String rank = lcPlayer.getRank().getDisplay();
                String bonus = lcPlayer.getBonus().getDisplay();
                String wolf = lcPlayer.getWolf().getDisplay();
                String hours = Utils.hoursToHoursAndMinutes(player.getStatistic(Statistic.PLAY_ONE_TICK) / 72000.0F);
                String jumps = (playerDataConfig.getInt("jumps") != -1) ? String.valueOf(playerDataConfig.getInt("jumps")) : "No jumps recorded yet...";
                String tags = (playerDataConfig.getInt("tags") != -1) ? "" + playerDataConfig.getStringList("tags").size() : "No tags recorded yet...";
                String pp = (int)Linkcraft.getPlugin().getEconomy().getBalance(player) + "pp";
                String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(player.getFirstPlayed()));
                sender.sendMessage(Utils.chat("&7Your LinkCraft Stats:\n&8&7Rank: " + rank + "\n&8&7Bonus: &b" + bonus + "\n&8&7Wolf: &b" + wolf + "\n&8&7Time Played: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
                return;
            }

            Player target = Bukkit.getPlayer(name);
            if(target != null && target.isOnline()) {
                LCPlayer lcPlayer = playerManager.getPlayer(target);
                FileConfiguration playerDataConfig = getFileConfiguration(target);
                String rank = lcPlayer.getRank().getDisplay();
                String bonus = lcPlayer.getBonus().getDisplay();
                String wolf = lcPlayer.getWolf().getDisplay();
                String hours = Utils.hoursToHoursAndMinutes(target.getStatistic(Statistic.PLAY_ONE_TICK) / 72000.0F);
                String jumps = (playerDataConfig.getInt("jumps") != -1) ? String.valueOf(playerDataConfig.getInt("jumps")) : "No jumps recorded yet...";
                String tags = (playerDataConfig.getInt("tags") != -1) ? "" + playerDataConfig.getStringList("tags").size() : "No tags recorded yet...";
                String pp = (int)Linkcraft.getPlugin().getEconomy().getBalance(target) + "pp";
                String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(target.getFirstPlayed()));
                sender.sendMessage(Utils.chat("&7" + target.getName() + "'s LinkCraft Stats:\n&8&7Rank: " + rank + "\n&8&7Bonus: &b" + bonus + "\n&8&7Wolf: &b" + wolf + "\n&8&7Time Played: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);

                if(offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
                    sender.sendMessage(Utils.chat("&cNo player stats found for '" + name + "'"));
                    return;
                }

                FileConfiguration playerDataConfig = getFileConfiguration(offlinePlayer);
                LCPlayer lcPlayer = playerManager.loadPlayer(offlinePlayer.getUniqueId().toString());
                String rank = lcPlayer.getRank().getDisplay();
                String wolf = lcPlayer.getWolf().getDisplay();
                String bonus = lcPlayer.getBonus().getDisplay();
                String hours = Utils.hoursToHoursAndMinutes(Utils.getStatistic(offlinePlayer, Constants.STAT_PLAY_ONE_TICK) / 72000.0F);
                String jumps = (playerDataConfig.getInt("jumps") != -1) ? String.valueOf(playerDataConfig.getInt("jumps")) : "No jumps recorded yet...";
                String tags = (playerDataConfig.getInt("tags") != -1) ? "" + playerDataConfig.getStringList("tags").size() : "No tags recorded yet...";
                String pp = (int)Linkcraft.getPlugin().getEconomy().getBalance(offlinePlayer) + "pp";
                String joinDate = (new SimpleDateFormat("d MMMM y")).format(new Date(offlinePlayer.getFirstPlayed()));
                sender.sendMessage(Utils.chat("&7" + name + "'s LinkCraft Stats:\n&8&7Rank: " + rank + "\n&8&7Bonus: &b" + bonus + "\n&8&7Wolf: &b" + wolf + "\n&8&7Time Played: &b" + hours + "\n&8&7Jumps: &b" + jumps + "\n&8&7Performance Points: &b" + pp + "\n&8&7Tags: &b" + tags + "\n&8&7Join Date: &b" + joinDate));
            }

        });
    }

    private FileConfiguration getFileConfiguration(OfflinePlayer player) {
        ConfigAccessor playerData = new ConfigAccessor(Linkcraft.getPlugin(), Linkcraft.getPlugin().getPlayerdataFolder(), player.getUniqueId().toString() + ".yml");
        FileConfiguration playerDataConfig = playerData.getConfig();
        if (playerDataConfig.get("jumps") == null) {
            playerDataConfig.set("jumps", Integer.valueOf(-1));
            playerDataConfig.set("tags", Integer.valueOf(-1));
            playerData.saveConfig();
        }
        return playerDataConfig;
    }
}
