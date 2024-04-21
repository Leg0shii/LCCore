package de.legoshi.lccore.command.maps;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.manager.PlayerManager;
import de.legoshi.lccore.menu.maps.LCMap;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Date;

@Register
@Command(names = {"complete"}, permission = "complete", desc = "<map> <player>")
public class CompleteCommand implements CommandClass {

    @Inject private MapManager mapManager;
    @Inject private PlayerManager playerManager;
    @Inject private DBManager db;

    @Command(names = "")
    public void complete(CommandSender sender, @ReflectiveTabComplete(clazz = MapManager.class, method = "getMapNames") String map,
                         @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String player,
                         @OptArg String nd) {

        Player victor = playerManager.playerByName(player);

        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            LCMap lcMap = mapManager.getMap(map);

            if(lcMap == null) {
                MessageUtil.send(Message.MAP_NOT_EXISTS, sender, map);
                return;
            }
            PlayerRecord record = playerManager.getPlayerRecord(victor, player);

            if(record == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, player);
                return;
            }

            String uuid = record.getUuid();



            LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(uuid);
            PlayerCompletion playerCompletion = new PlayerCompletion();
            playerCompletion.setPlayer(lcPlayerDB);
            playerCompletion.setMap(map);

            boolean hasPrevious = false;

            if(mapManager.hasPreviousCompletion(uuid, map)) {
                hasPrevious = true;
                playerCompletion = mapManager.getPreviousCompletion(uuid, map);
            }

            if(requiresBan()) {
                playerCompletion.setCompletions(0);
                db.persist(playerCompletion, lcPlayerDB);
                return;
            }

            if(nd == null) {
                playerCompletion.setLatest(new Date());
                if(!hasPrevious) {
                    playerCompletion.setFirst(new Date());
                }
            }

            if(hasPrevious) {
                playerCompletion.setCompletions(playerCompletion.getCompletions() + 1);
                db.update(playerCompletion);
                MessageUtil.send(Message.MAP_NEW_COMPLETION, sender, map, record.getName());
            } else {
                playerCompletion.setCompletions(1);
                db.persist(playerCompletion, lcPlayerDB);
                MessageUtil.send(Message.MAP_FIRST_COMPLETION, sender, map, record.getName());
            }
        });
    }

    // TODO: ban logic
    private boolean requiresBan() {
        return false;
    }

//    private void ban(OfflinePlayer offlinePlayer) {
//        Bukkit.getBanList(BanList.Type.NAME).addBan(offlinePlayer.getName(), "You beat a hard map, notify staff!", null, "map completion");
//        Player player = Bukkit.getPlayer(offlinePlayer.getName());
//        if(player != null && player.isOnline()) {
//            player.kickPlayer("You beat a hard map, notify staff!");
//        }
//    }

//    private void giveRewards(Player player, LCMap map) {
//        String warp = "spawn"; // = map.warp != null ? map.warp : "spawn"
//        serverCommand("warp " + warp + " " + player.getName());
//
//        if(map.pp > 0) {
//            serverCommand("eco give " + player.getName() + " " + map.pp);
//        }
//
//        if(map.star_rating >= 7) {
//            Bukkit.broadcastMessage(parkour + " §a" + player.getName() + " §r§7" + "has finished §r" + Utils.chat(map.name) + "§r§7! " + "§r§e§l" + map.star_rating + "*");
//        } else if(map.star_rating >= 3.5) {
//            Bukkit.broadcastMessage(parkour + " §a" + player.getName() + " §r§7" + "has finished §r" + Utils.chat(map.name) + "§r§7! " + "§r§e" + map.star_rating + "*");
//        } else {
//            player.sendMessage(parkour + " §7You have just completed §r" + Utils.chat(map.name) + "§r§7! " + "§r§e" + map.star_rating + "*");
//        }
//    }
//
//    private void serverCommand(String command) {
//        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
//    }
}
