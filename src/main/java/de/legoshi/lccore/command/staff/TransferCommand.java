package de.legoshi.lccore.command.staff;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.command.flow.annotated.annotation.ReflectiveTabComplete;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.*;
import de.legoshi.lccore.manager.*;
import de.legoshi.lccore.player.PlayerRecord;
import de.legoshi.lccore.player.PlayerSave;
import de.legoshi.lccore.player.display.BonusDTO;
import de.legoshi.lccore.player.display.LCPlayer;
import de.legoshi.lccore.player.display.RankDTO;
import de.legoshi.lccore.player.display.WolfDTO;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.Utils;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Register
@Command(names = {"transfer"}, permission = "transfer", desc = "<player> <player>")
public class TransferCommand implements CommandClass {

    @Inject private PlayerManager playerManager;
    @Inject private EssentialsManager essentialsManager;
    @Inject private PracticeManager practiceManager;
    @Inject private MapManager mapManager;
    @Inject private TagManager tagManager;
    @Inject private DBManager db;
    @Inject private LuckPermsManager lpManager;

    @Command(names = "")
    public void transfer(CommandSender sender, @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String from,
                         @ReflectiveTabComplete(clazz = PlayerManager.class, method = "getPossibleNames", player = true) String to) {
        Player fromPlayer = playerManager.playerByName(from);
        Player toPlayer = playerManager.playerByName(to);
        Linkcraft.async(() -> {
            if(fromPlayer != null) {
                MessageUtil.send(Message.MUST_BE_OFFLINE, sender, from);
                return;
            }

            if(toPlayer != null) {
                MessageUtil.send(Message.MUST_BE_OFFLINE, sender, to);
                return;
            }


            PlayerRecord fromRecord = playerManager.getPlayerRecord(null, from);
            PlayerRecord toRecord = playerManager.getPlayerRecord(null, to);


            if(fromRecord == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, from);
                return;
            }

            if(toRecord == null) {
                MessageUtil.send(Message.NEVER_JOINED, sender, to);
                return;
            }

            LCPlayer fromLCPlayer = playerManager.loadPlayer(fromRecord.getUuid());
            LCPlayer toLCPlayer = playerManager.loadPlayer(toRecord.getUuid());

            // Player statistics
            Utils.mergeStatistics(fromRecord, toRecord);


            // Current location
            try {
                Location fromLoc = playerManager.getOTPLocationNBT(fromRecord.getUuid());
                Location toLoc = playerManager.getOTPLocationNBT(toRecord.getUuid());
                Location spawnLoc = essentialsManager.getWarpLocation("spawn");

                MessageUtil.log(Message.MOVING_POSITION, true, toRecord.getName(), Utils.getStringFromLocation(toLoc), Utils.getStringFromLocation(fromLoc));
                MessageUtil.log(Message.MOVING_POSITION, true, fromRecord.getName(), Utils.getStringFromLocation(fromLoc), Utils.getStringFromLocation(spawnLoc));
                playerManager.setOfflinePlayerLocationNBT(toRecord.getUuid(), fromLoc);
                playerManager.setOfflinePlayerLocationNBT(fromRecord.getUuid(), spawnLoc);
            } catch (Exception ignored) {

            }

            // Items
            try {
                playerManager.transferItems(fromRecord.getUuid(), toRecord.getUuid());
                MessageUtil.log("Items successfully transferred from " + fromRecord.getName() + " to " + toRecord.getName(), true);
            } catch (Exception e) {
                MessageUtil.log("Error occurred while transferring items from " + fromRecord.getName() + " to " + toRecord.getName(), true);
            }

            // Practice location
            Location fromPracLoc = practiceManager.getPracticeLocation(fromRecord.getUuid());
            Location toPracLoc = practiceManager.getPracticeLocation(toRecord.getUuid());

            if(fromPracLoc != null) {
                if(toPracLoc != null) {
                    MessageUtil.log(Message.UNPRACTICE_FOR_AT, true, toRecord.getName(), Utils.getStringFromLocation(toPracLoc));
                    practiceManager.forceUnpractice(toRecord.getUuid());
                }

                MessageUtil.log(Message.UNPRACTICE_FOR_AT, true, fromRecord.getName(), Utils.getStringFromLocation(fromPracLoc));
                practiceManager.forceUnpractice(fromRecord.getUuid());

                MessageUtil.log(Message.PRACTICE_FOR_AT, true, toRecord.getName(), Utils.getStringFromLocation(fromPracLoc));
                practiceManager.updatePracticeLocation(toRecord.getUuid(), fromPracLoc);
                practiceManager.givePracticeGroup(toRecord.getUuid());
            }


            // PP
            double fromPp = essentialsManager.getPp(fromRecord.getName());
            double toPp = essentialsManager.getPp(toRecord.getName());

            MessageUtil.log(Message.TRANSFER_PP, true, fromPp, fromRecord.getName(), toPp, toRecord.getName(), fromPp + toPp);
            essentialsManager.setPp(toRecord.getName(), fromPp + toPp);

            MessageUtil.log(Message.DELETE_PP, true, fromRecord.getName(), fromPp);
            essentialsManager.setPp(fromRecord.getName(), 0);

            // Saves
            List<PlayerSave> fromSaves = playerManager.getSavesFor(fromRecord.getUuid());
            playerManager.giveSaves(fromRecord, toRecord, fromSaves);

            MessageUtil.log(Message.DELETE_SAVES, true, fromRecord.getName());
            playerManager.clearSavesFor(fromRecord.getUuid());

            // Tags
            List<Tag> fromTags = tagManager.getOwnedTags(fromRecord.getUuid());
            List<Tag> toTags = tagManager.getOwnedTags(toRecord.getUuid());
            List<Tag> transferTags = new ArrayList<>();

            Set<String> toTagIds = toTags.stream()
                    .map(Tag::getId)
                    .collect(Collectors.toSet());

            for (Tag fromTag : fromTags) {
                if (!toTagIds.contains(fromTag.getId())) {
                    transferTags.add(fromTag);
                }
            }

            for(Tag transferTag : transferTags) {
                MessageUtil.log(Message.TRANSFER_TAGS, true, toRecord.getName(), transferTag.getId());
                Tag tag = tagManager.getTag(transferTag.getId());
                LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(toRecord.getUuid());
                db.persist(new PlayerTag(tag, lcPlayerDB), lcPlayerDB, tag);
            }

            tagManager.removeAllTagsFrom(fromRecord.getUuid());
            MessageUtil.log(Message.DELETE_TAGS, true, fromRecord.getName());


            // Preferences
            PlayerPreferences fromPrefs = playerManager.getPlayerPrefs(fromRecord.getUuid());
            PlayerPreferences toPrefs = new PlayerPreferences(fromPrefs);
            toPrefs.setId(toRecord.getUuid());
            db.update(toPrefs);
            db.delete(fromPrefs);
            db.persist(new PlayerPreferences(fromRecord.getUuid()));


            // Completions
            Map<String, PlayerCompletion> fromCompletions = mapManager.getPlayerMapData(fromRecord.getUuid());
            Map<String, PlayerCompletion> toCompletions = mapManager.getPlayerMapData(toRecord.getUuid());

            for(Map.Entry<String, PlayerCompletion> completion : fromCompletions.entrySet()) {
                String mapId = completion.getKey();
                PlayerCompletion completionData = completion.getValue();

                if(toCompletions.get(mapId) != null) {
                    PlayerCompletion toCompletion = toCompletions.get(mapId);
                    toCompletion.setCompletions(toCompletion.getCompletions() + completionData.getCompletions());
                    if (completionData.getLatest() != null && (toCompletion.getLatest() == null || completionData.getLatest().compareTo(toCompletion.getLatest()) > 0)) {
                        toCompletion.setLatest(completionData.getLatest());
                    }

                    if (completionData.getFirst() != null && (toCompletion.getFirst() == null || completionData.getFirst().compareTo(toCompletion.getFirst()) < 0)) {
                        toCompletion.setFirst(completionData.getFirst());
                    }

                    db.update(toCompletion);
                } else {
                    LCPlayerDB toLCDBPlayer = new LCPlayerDB(toRecord.getUuid());
                    completionData.setPlayer(toLCDBPlayer);
                    db.persist(completionData, toLCDBPlayer);
                }
                MessageUtil.log(Message.TRANSFER_COMPLETION, true, toRecord.getName(), completionData.getCompletions(), mapId);
            }

            mapManager.clearCompletionData(fromRecord.getUuid());
            MessageUtil.log(Message.DELETE_COMPLETIONS, true, fromRecord.getName());


            // Rank
            if(fromLCPlayer.getRank().getPosition() > toLCPlayer.getRank().getPosition()) {
                String rankKey = fromLCPlayer.getRank().getKey();
                lpManager.giveGroup(toRecord.getUuid(), rankKey);
                MessageUtil.log(Message.TRANSFER_RANK, true, toRecord.getName(), toLCPlayer.getRank().getDisplay(), fromLCPlayer.getRank().getDisplay());
            }


            RankDTO lowestRank = null;
            for(RankDTO rank : ConfigManager.ranksDisplay.values()) {
                if(rank.getPosition() == 0) {
                    lowestRank = rank;
                    continue;
                }
                if(lpManager.hasGroup(fromRecord.getUuid(), rank.getKey())) {
                    lpManager.removeGroup(fromRecord.getUuid(), rank.getKey());
                }
            }

            if(lowestRank != null) {
                MessageUtil.log(Message.DELETE_RANK, true, fromRecord.getName(), lowestRank.getDisplay());
            }

            // Bonus
            BonusDTO lowestBonus = null;
            for(BonusDTO bonus : ConfigManager.bonusDisplay.values()) {
                if(bonus.getPosition() == 0) {
                    lowestBonus = bonus;
                }
                if(lpManager.hasPermission(fromRecord.getUuid(), bonus.getKey()) && !lpManager.hasPermission(toRecord.getUuid(), bonus.getKey())) {
                    lpManager.givePermission(toRecord.getUuid(), bonus.getKey());
                    MessageUtil.log(Message.TRANSFER_BONUS, true, bonus.getDisplay(), toRecord.getName());
                }
            }

            if(lowestBonus != null) {
                MessageUtil.log(Message.DELETE_BONUS, true, fromRecord.getName(), lowestBonus.getDisplay());
            }

            // Wolf
            if(fromLCPlayer.getWolf().getPosition() > toLCPlayer.getWolf().getPosition()) {
                String wolfKey = fromLCPlayer.getWolf().getKey();
                if(toLCPlayer.getWolf().getPosition() > 0) {
                    lpManager.removeGroup(toRecord.getUuid(), toLCPlayer.getWolf().getKey());
                }
                lpManager.giveGroup(toRecord.getUuid(), wolfKey);
                MessageUtil.log(Message.TRANSFER_WOLF, true, toRecord.getName(), fromLCPlayer.getWolf().getDisplay(), toLCPlayer.getWolf().getDisplay());
            }

            WolfDTO lowestWolf = null;

            for(WolfDTO wolf : ConfigManager.wolfDisplay.values()) {
                if(wolf.getPosition() == 0) {
                    lowestWolf = wolf;
                }

                if(lpManager.hasGroup(fromRecord.getUuid(), wolf.getKey())) {
                    lpManager.removeGroup(fromRecord.getUuid(), wolf.getKey());
                }
            }

            if(lowestWolf != null) {
                MessageUtil.log(Message.DELETE_WOLF, true, fromRecord.getName(), lowestWolf.getDisplay());
            }

            // Checkpoints
            Map<String, PlayerCheckpoint> fromCheckpoints = mapManager.getPlayerCheckpointMap(fromRecord.getUuid());
            Map<String, PlayerCheckpoint> toCheckpoints = mapManager.getPlayerCheckpointMap(toRecord.getUuid());

            for(Map.Entry<String, PlayerCheckpoint> checkpointEntry : fromCheckpoints.entrySet()) {
                String map = checkpointEntry.getKey();
                PlayerCheckpoint fromCheckpoint = checkpointEntry.getValue();

                if(toCheckpoints.get(map) != null) {
                    PlayerCheckpoint toCheckpoint = toCheckpoints.get(map);
                    MessageUtil.log(Message.TRANSFER_OVERWRITE_CHECKPOINT, true, toRecord.getName(), toCheckpoint.getCheckpointMap(), Utils.getStringFromLocation(toCheckpoint.getLocation().toSpigot()), Utils.getStringFromLocation(fromCheckpoint.getLocation().toSpigot()));
                    db.delete(toCheckpoint);

                }
                LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(toRecord.getUuid());
                LCLocation newLocationDB = new LCLocation(fromCheckpoint.getLocation().toSpigot());
                db.persist(newLocationDB);
                PlayerCheckpoint newCheckpoint = new PlayerCheckpoint();
                newCheckpoint.setLocation(newLocationDB);
                newCheckpoint.setCheckpointMap(map);
                newCheckpoint.setPlayer(lcPlayerDB);
                db.persist(newCheckpoint, lcPlayerDB, newLocationDB);
                MessageUtil.log(Message.TRANSFER_CHECKPOINT, true, toRecord.getName(), newCheckpoint.getCheckpointMap(), Utils.getStringFromLocation(newCheckpoint.getLocation().toSpigot()));
            }

            mapManager.deletePlayerCheckpoints(fromRecord.getUuid());
            MessageUtil.log(Message.DELETE_CHECKPOINTS, true, fromRecord.getName());

            // Stars
            List<String> fromStars = playerManager.ownedStars(fromRecord.getUuid());
            List<String> toStars = playerManager.ownedStars(toRecord.getUuid());

            for (String star : fromStars) {
                if(!toStars.contains(star)) {
                    playerManager.unlockStar(toRecord.getUuid(), star);
                    MessageUtil.log(Message.TRANSFER_STAR, true, toRecord.getName(), star);
                }
            }

            playerManager.deleteStars(fromRecord.getUuid());
            MessageUtil.log(Message.DELETE_STARS, true, fromRecord.getName());


            // Chat Colours
            List<String> fromColours = playerManager.ownedColors(fromRecord.getUuid());
            List<String> toColours = playerManager.ownedColors(toRecord.getUuid());

            for (String colour : fromColours) {
                if(!toColours.contains(colour)) {
                    playerManager.unlockColor(toRecord.getUuid(), colour);
                    MessageUtil.log(Message.TRANSFER_STAR, true, toRecord.getName(), colour);
                }
            }

            playerManager.deleteColours(fromRecord.getUuid());
            MessageUtil.log(Message.DELETE_CHAT_COLORS, true, fromRecord.getName());


            MessageUtil.log(Message.TRANSFER_COMPLETE, true, fromRecord.getName(), toRecord.getName());
        });
    }
}
