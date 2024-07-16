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

        if(nd != null) {
            Linkcraft.async(() -> {
                mapManager.giveCompletionNoRewards(sender, record, lcMap, nd.equalsIgnoreCase("d"));
            });
            return;
        }

        if(victor == null || !victor.isOnline()) {
            MessageUtil.send(Message.MAP_CANT_GIVE_COMPLETION_OFFLINE, sender);
            return;
        }

        String uuid = record.getUuid();
        PlayerCompletion playerCompletion = mapManager.getPreviousCompletion(uuid, map);
        boolean firstCompletion = playerCompletion == null;
        LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(uuid);

        if(mapManager.requiresBanToComplete(victor, lcMap, playerCompletion)) {
            if(!firstCompletion) {
                playerCompletion.setReceivedBan(true);
                db.update(playerCompletion);
            } else {
                PlayerCompletion completion = new PlayerCompletion();
                completion.setPlayer(lcPlayerDB);
                completion.setMap(map);
                completion.setReceivedBan(true);
                db.persist(completion, lcPlayerDB);
            }
            Linkcraft.consoleCommand("ban " + victor.getName() + " -s " + "§4You beat a hard map! §7Notify staff on §9Discord§r");
            MessageUtil.discord(record.getName() + " received a ban for completing: " + map, "completions");
            return;
        }

        String warp = lcMap.getVictoryWarp() == null ? "spawn" : lcMap.getVictoryWarp();
        if(warp.equalsIgnoreCase("bonus")) {
            Linkcraft.consoleCommand("warp " + warp + " " + victor.getName() + " " + "true");
        } else {
            Linkcraft.consoleCommand("warp " + warp + " " + victor.getName());
        }

        if(firstCompletion) {
            playerCompletion = new PlayerCompletion();
            playerCompletion.setPlayer(lcPlayerDB);
            playerCompletion.setMap(map);
            playerCompletion.setCompletions(1);
            playerCompletion.setFirst(new Date());
            playerCompletion.setLatest(new Date());
            playerCompletion.setReceivedBan(false);
            db.persist(playerCompletion, lcPlayerDB);
            MessageUtil.send(Message.MAP_FIRST_COMPLETION, sender, map, record.getName());
        } else {
            if(playerCompletion.isReceivedBan() && playerCompletion.getCompletions() == 0) {
                playerCompletion.setFirst(new Date());
            }
            playerCompletion.setCompletions(playerCompletion.getCompletions() + 1);
            playerCompletion.setLatest(new Date());
            playerCompletion.setReceivedBan(false);
            db.update(playerCompletion);
            MessageUtil.send(Message.MAP_NEW_COMPLETION, sender, map, record.getName());
        }
        MessageUtil.discord(record.getName() + " completed: " + map, "completions");

        mapManager.giveRewards(victor, lcMap);
    }

}
