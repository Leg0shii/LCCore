package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.composite.PlayerCheckpointId;
import de.legoshi.lccore.database.models.LCLocation;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.database.models.PlayerCheckpoint;
import de.legoshi.lccore.player.checkpoint.CheckpointDTO;
import de.legoshi.lccore.util.MusicHelper;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.io.File;

public class CheckpointManager {
    @Inject private LuckPermsManager lpManager;
    @Inject private PlayerManager playerManager;
    @Inject private DBManager db;

    // TODO: Create player_checkpoints DB table. Store player_id(PKFK), cp_map_id(PK), location_id(FK)
    // Keep checkpoint signs defined in file wtv

    public void init() {
        lpManager.createDefaultCheckpointGroup();
    }

    public boolean isCheckpointSign(Block b) {
        return (b.getType().equals(Material.SIGN) || b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN)) && checkpointFileExists(b);
    }

    private String getCheckpointFileName(Block b) {
        return b.getWorld().getName() + "_" + b.getX() + "_" + b.getY() + "_" + b.getZ() + ".yml";
    }

    private String getCheckpointFilePath(Block b) {
        return Linkcraft.getPlugin().getPluginFolder().getAbsolutePath() + File.separator + "checkpoint_signs" + File.separator + getCheckpointFileName(b);
    }

    private File getCheckpointFile(Block b) {
        return new File(getCheckpointFilePath(b));
    }

    private boolean checkpointFileExists(Block b) {
        return getCheckpointFile(b).exists();
    }

    private boolean canUseCheckpoints(Player player) {
        return player.hasPermission("linkcraft.checkpoint");
    }

    private CheckpointDTO getCheckpointData(Block b) {
        return new CheckpointDTO(YamlConfiguration.loadConfiguration(getCheckpointFile(b)));
    }

    private Location getCheckpointCentre(Block b) {
        Location location = b.getLocation();
        location.add(0.5D, 0D, 0.5D);
        return location;
    }

    private void savePlayerCheckpoint(Player player, Location location) {
        String map = getCurrentCheckpointMap(player);
        if(map == null) {
            MessageUtil.send(Message.CHECKPOINT_SAVING_ERROR, player);
            return;
        }

        if(!hasPreviousCheckpointDataForMap(player, map)) {
            PlayerCheckpoint playerCheckpoint = new PlayerCheckpoint();
            LCPlayerDB lcDBPlayer = playerManager.getPlayerDB(player);
            playerCheckpoint.setPlayer(lcDBPlayer);
            playerCheckpoint.setCheckpointMap(map);
            LCLocation lcLocation = new LCLocation(location);
            db.persist(lcLocation);
            playerCheckpoint.setLocation(lcLocation);
            db.persist(playerCheckpoint, lcDBPlayer, lcLocation);
        } else {
            PlayerCheckpoint playerCheckpoint = getPlayerCheckpointForMap(player, map);
            LCLocation oldLocation = playerCheckpoint.getLocation();
            playerCheckpoint.setLocation(null);
            db.update(playerCheckpoint);
            db.delete(oldLocation);
            LCLocation lcLocation = new LCLocation(location);
            db.persist(lcLocation);
            playerCheckpoint.setLocation(lcLocation);
            db.update(playerCheckpoint);
        }
    }

    public void deletePlayerCheckpoint(Player player) {
        PlayerCheckpoint playerCheckpoint = getPlayerCheckpoint(player);
        if(playerCheckpoint != null) {
            LCLocation loc = playerCheckpoint.getLocation();
            db.delete(playerCheckpoint);
            db.delete(loc);
        }
    }

    public PlayerCheckpoint getPlayerCheckpoint(Player player) {
        String currMap = getCurrentCheckpointMap(player);

        if(currMap == null) {
            return null;
        }

        return getPlayerCheckpointForMap(player, currMap);
    }

    public PlayerCheckpoint getPlayerCheckpointForMap(Player player, String map) {
        return db.find(new PlayerCheckpointId(player.getUniqueId().toString(), map), PlayerCheckpoint.class);
    }

    private boolean hasPreviousCheckpointDataForMap(Player player, String map) {
        return getPlayerCheckpointForMap(player, map) != null;
    }

    private String getCurrentCheckpointMap(Player player) {
        return playerManager.getPlayerDB(player).getCurrentCheckpointMap();
    }

    public void clearCurrentCheckpointMap(Player player) {
        if(getCurrentCheckpointMap(player) != null) {
            LCPlayerDB lcPlayerDB = playerManager.getPlayerDB(player);
            lcPlayerDB.setCurrentCheckpointMap(null);
            db.update(lcPlayerDB);
        }
    }

    public void useCheckpoint(Player player, Block block) {
        if(!canUseCheckpoints(player)) {
            MessageUtil.send(Message.NO_CHECKPOINT_PERM, player, false);
            return;
        }

        CheckpointDTO checkpointDTO = getCheckpointData(block);
        Location location = null;

        switch (checkpointDTO.getType()) {
            case "direct":
                location = player.getLocation();
                break;
            case "ground":
                if(!playerManager.isOnGround(player) || playerManager.isFalling(player)) {
                    MessageUtil.send(Message.CHECKPOINT_GROUND, player, false);
                    return;
                }
                location = player.getLocation();
                break;
            case "here":
                location = getCheckpointCentre(block);
                break;
            default:
                return;
        }

        savePlayerCheckpoint(player, location);
        MusicHelper.playMusic(player);
        MessageUtil.send(Message.GET_CHECKPOINT, player, false);
    }

    public void giveCheckpointGroup(Player player) {
        lpManager.giveGroup(player, "checkpoint");
    }

    public void removeCheckpointGroup(Player player) {
        if(lpManager.hasGroup(player, "checkpoint")) {
            lpManager.removeGroup(player, "checkpoint");
        }
    }

    public void exitCheckpointMode(Player player) {
        removeCheckpointGroup(player);
        clearCurrentCheckpointMap(player);
    }
}
