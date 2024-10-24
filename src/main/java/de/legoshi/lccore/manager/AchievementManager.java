package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.achievements.AchievementDifficulty;
import de.legoshi.lccore.achievements.AchievementType;
import de.legoshi.lccore.achievements.requirement.UnlockRequirement;
import de.legoshi.lccore.achievements.requirement.UnlockRequirementType;
import de.legoshi.lccore.achievements.reward.Reward;
import de.legoshi.lccore.achievements.reward.RewardType;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AchievementManager {
    @Inject private Injector injector;
    @Getter private List<Achievement> achievements = new ArrayList<>();

    public List<Achievement> getAchievementsByType(AchievementType type) {
        return achievements.stream()
                .filter(achievement -> achievement.getType() == type)
                .collect(Collectors.toList());
    }

    public void loadAchievements() {
        achievements.clear();
        FileConfiguration data = new ConfigAccessor(Linkcraft.getPlugin(), "achievements.yml").getConfig();
        List<Map<?, ?>> achievementsData = data.getMapList("achievements");

        if (achievementsData == null || achievementsData.isEmpty()) {
            MessageUtil.log(Message.CONFIG_LOADING_ERR, true, "Achievements Configuration File");
            return;
        }


        for (Map<?, ?> achievementData : achievementsData) {
            Achievement achievement = new Achievement();

            String name = (String)achievementData.get("name");
            String id = name.replace(" ", "");
            int points = (Integer)achievementData.get("points");
            String description = (String)achievementData.get("description");
            AchievementType achievementType = AchievementType.valueOf((String)achievementData.get("type"));
            List<UnlockRequirement> requirements = new ArrayList<>();
            List<Reward> rewards = new ArrayList<>();

            achievement.setName(name);
            achievement.setId(id);
            achievement.setPoints(points);
            achievement.setType(achievementType);
            achievement.setDifficulty(AchievementDifficulty.byPoints(points));
            achievement.setDescription(description);

            List<Map<?, ?>> requirementsData = (List<Map<?,?>>)achievementData.get("requirements");
            List<Map<?, ?>> rewardsData = (List<Map<?,?>>)achievementData.get("rewards");

            for(Map<?, ?> requirementData : requirementsData) {
                UnlockRequirementType type = UnlockRequirementType.valueOf((String)requirementData.get("type"));
                UnlockRequirement req = injector.getInstance(type.mappedClazz);
                req.init(requirementData);
                requirements.add(req);
            }

            for(Map<?, ?> rewardData : rewardsData) {
                RewardType type = RewardType.valueOf((String)rewardData.get("type"));
                Reward reward = injector.getInstance(type.mappedClazz);
                reward.init(rewardData);
                rewards.add(reward);
            }
            achievement.setRequirements(requirements);
            achievement.setRewards(rewards);
            achievements.add(achievement);
        }
    }
}
