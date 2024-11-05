package de.legoshi.lccore.command.staff.location;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.TabComplete;
import de.legoshi.lccore.command.staff.location.types.*;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.menu.staff.LocationManageHolder;
import de.legoshi.lccore.player.PlayerClipboard;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.player.PlayerSave;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Register
@Command(names = {"locmanage", "locationmanage"}, permission = "locmanage", desc = "")
public class LocationManageCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private PracticeManager practiceManager;
    @Inject private Injector injector;

    public static HashMap<String, List<LocationManageRecord>> cached = new HashMap<>();
    private static HashMap<String, BukkitTask> tasks = new HashMap<>();

    @Command(names = "")
    public void locationManage(CommandSender sender, @OptArg @TabComplete(suggestions = {"reset"}) String option) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;

            if(cached.get(player.getUniqueId().toString()) != null && !"reset".equalsIgnoreCase(option)) {
                injector.getInstance(LocationManageHolder.class).openGui(player, null);
                return;
            }

            BukkitTask existingTask = tasks.get(player.getUniqueId().toString());
            if (existingTask != null) {
                existingTask.cancel();
                tasks.remove(player.getUniqueId().toString());
            }

            PlayerClipboard clipboard = playerManager.getClipboard(player);

            if(clipboard == null) {
                MessageUtil.send(Message.NO_CLIPBOARD, player);
                return;
            }
            if(clipboard.getPos1() == null) {
                MessageUtil.send(Message.CLIPBOARD_POS_UNSET, player, "1");
                return;
            }

            if(clipboard.getPos2() == null) {
                MessageUtil.send(Message.CLIPBOARD_POS_UNSET, player, "2");
                return;
            }


            List<LocationManageRecord> records = getOnlinePlayersWithinClipboard(clipboard);
            records.addAll(getOfflinePlayersWithinClipboard(clipboard));
            records.addAll(getPlayerSavesWithinClipboard(clipboard));

            BukkitTask task = Linkcraft.asyncLater(() -> {
                cached.remove(player.getUniqueId().toString());
                tasks.remove(player.getUniqueId().toString());
                MessageUtil.send("Location Manage Cache Expired", player);
            }, 20L * 60 * 30);

            tasks.put(player.getUniqueId().toString(), task);
            cached.put(player.getUniqueId().toString(), records);
            injector.getInstance(LocationManageHolder.class).openGui(player, null);
        });
    }

    private List<LocationManageRecord> getOfflinePlayersWithinClipboard(PlayerClipboard clipboard) {
        List<LocationManageRecord> records = new ArrayList<>();
        for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if(!p.isOnline()){
                try {
                    // Direct call to NBT data to avoid DB table lookup
                    //Location l = playerManager.getOTPLocationNBT(p.getUniqueId().toString());
                    Location l = playerManager.NBTLocationToSpigotLocation(p.getUniqueId().toString());

                    if(l == null) {
                        continue;
                    }

                    Location pracL = practiceManager.getPracticeLocation(p.getUniqueId().toString());
                    if(clipboard.isLocationWithin(l)) {
                        records.add(new OfflinePlayerRecord(p.getUniqueId().toString(), p.getName(), l, pracL));
                    }
                } catch (Exception ignored) {}
            }
        }

        return records;
    }

    private List<LocationManageRecord> getPlayerSavesWithinClipboard(PlayerClipboard clipboard) {
        List<LocationManageRecord> records = new ArrayList<>();
        File dataFolder = Linkcraft.getPlugin().getPlayerdataFolder();
        if(dataFolder == null) {
            return records;
        }

        for(String playerFileName : dataFolder.list()) {
            PlayerRecord record = playerManager.recordForUuid(Utils.removeYMLExtension(playerFileName));
            for(PlayerSave save : playerManager.getSavesFor(playerFileName)) {
                if(clipboard.isLocationWithin(save.getLocation())) {
                    records.add(new SaveLocationRecord(record.getUuid(), record.getName(), save));
                }
            }
        }
        return records;
    }

    private List<LocationManageRecord> getOnlinePlayersWithinClipboard(PlayerClipboard clipboard) {
        List<LocationManageRecord> players = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(clipboard.isLocationWithin(p.getLocation())) {
                players.add(injector.getInstance(OnlinePlayerRecord.class).init(p.getUniqueId().toString(), p.getName()));
            }
        }
        return players;
    }

    public static void clearCache(String uuid) {
        BukkitTask existingTask = tasks.get(uuid);
        if (existingTask != null) {
            existingTask.cancel();
            tasks.remove(uuid);
        }
        cached.remove(uuid);
    }
}
