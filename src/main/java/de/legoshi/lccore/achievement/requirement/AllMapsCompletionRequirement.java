package de.legoshi.lccore.achievement.requirement;

import de.legoshi.lccore.achievement.progress.Progress;
import de.legoshi.lccore.database.models.PlayerCompletion;
import de.legoshi.lccore.manager.MapManager;
import de.legoshi.lccore.menu.maps.LCMap;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllMapsCompletionRequirement implements UnlockRequirement {

    @Inject private MapManager mapManager;
    private List<String> requiredMaps;

    public void init(List<String> requiredMaps) {
        this.requiredMaps = requiredMaps;
    }

    @Override
    public boolean isSatisfied(Player player) {
        Map<String, PlayerCompletion> mapData = mapManager.getPlayerMapData(player.getUniqueId().toString());
        for(String mapId : requiredMaps) {
            PlayerCompletion completion = mapData.get(mapId);
            if(completion == null || completion.getCompletions() < 1) {
                return false;
            }
        }
        return true;
    }

    // TODO: perhaps think of a method to not have to call the same method (mapManager.getPlayerMapData()) maybe
    // we could do something like initPlayer(player) and that inits the player related data for the unlock requirement?
    @Override
    public Progress getProgress(Player player) {
        Map<String, PlayerCompletion> mapData = mapManager.getPlayerMapData(player.getUniqueId().toString());
        List<String> incomplete = new ArrayList<>();
        List<String> complete = new ArrayList<>();

        for(String mapId : requiredMaps) {
            PlayerCompletion completion = mapData.get(mapId);
            if(completion == null || completion.getCompletions() < 1) {
                incomplete.add(mapId);
            } else {
                complete.add(mapId);
            }
        }
        return null;
    }
}
