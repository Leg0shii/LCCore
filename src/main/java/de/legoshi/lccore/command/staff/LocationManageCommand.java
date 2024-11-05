package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.manager.PracticeManager;
import de.legoshi.lccore.player.PlayerClipboard;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.player.PlayerSave;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Register
@Command(names = {"locmanage", "locationmanage"}, permission = "locmanage", desc = "")
public class LocationManageCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private PracticeManager practiceManager;

    @Command(names = "")
    public void locationManage(CommandSender sender) {
        Linkcraft.async(() -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;

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


            List<Player> online = getOnlinePlayersWithinClipboard(clipboard);
            List<PlayerRecord> offline = getOfflinePlayersWithinClipboard(clipboard);
            Map<PlayerRecord, List<PlayerSave>> saves = getPlayerSavesWithinClipboard(clipboard);

            for(Player p : online) {
                String name = p.getName();
                if(practiceManager.isInPractice(p)) {
                    name += " (Prac)";
                }
                MessageUtil.send(name + " - Online", player);
            }

            for(PlayerRecord p : offline) {
                String name = p.getName();
                if(practiceManager.isInPractice(p.getUuid())) {
                    name += " (Prac)";
                }
                MessageUtil.send(name + " - Offline", player);
            }

            for(Map.Entry<PlayerRecord, List<PlayerSave>> entry : saves.entrySet()) {
                PlayerRecord record = entry.getKey();
                List<PlayerSave> playerSaves = entry.getValue();

                for(PlayerSave save : playerSaves) {
                    String name = record.getName() + " - Save";
                    MessageUtil.send(name + " (" + save.getName() + ")", player);
                }
            }
        });
    }

    private List<PlayerRecord> getOfflinePlayersWithinClipboard(PlayerClipboard clipboard) {
        List<PlayerRecord> records = new ArrayList<>();
        for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if(!p.isOnline()){
                try {
                    Location l = playerManager.getOTPLocationNBT(p.getUniqueId().toString());
                    if(clipboard.isLocationWithin(l)) {
                        records.add(new PlayerRecord(p.getUniqueId().toString(), p.getName()));
                    }
                } catch (IOException ignored) {}

            }
        }

        return records;
    }

    private Map<PlayerRecord, List<PlayerSave>> getPlayerSavesWithinClipboard(PlayerClipboard clipboard) {
        Map<PlayerRecord, List<PlayerSave>> playersSaveData = new HashMap<>();
        File dataFolder = Linkcraft.getPlugin().getPlayerdataFolder();
        if(dataFolder == null) {
            return playersSaveData;
        }

        for(String playerFileName : dataFolder.list()) {
            List<PlayerSave> savesWithinClipboard = new ArrayList<>();
            String uuid = Utils.removeYMLExtension(playerFileName);
            List<PlayerSave> saves = playerManager.getSavesFor(playerFileName);
            for(PlayerSave save : saves) {
                if(clipboard.isLocationWithin(save.getLocation())) {
                    savesWithinClipboard.add(save);
                }
            }
            PlayerRecord record = playerManager.recordForUuid(uuid);
            playersSaveData.put(record, savesWithinClipboard);
        }
        return playersSaveData;
    }

    private List<Player> getOnlinePlayersWithinClipboard(PlayerClipboard clipboard) {
        List<Player> players = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(clipboard.isLocationWithin(p.getLocation())) {
                players.add(p);
            }
        }
        return players;
    }
}
