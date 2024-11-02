package de.legoshi.lccore.manager;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.achievements.AchievementDifficulty;
import de.legoshi.lccore.achievements.AchievementType;
import de.legoshi.lccore.achievements.requirement.UnlockRequirement;
import de.legoshi.lccore.achievements.requirement.UnlockRequirementType;
import de.legoshi.lccore.achievements.reward.Reward;
import de.legoshi.lccore.achievements.reward.RewardType;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.PlayerAchievementPoints;
import de.legoshi.lccore.database.models.PlayerAchievements;
import de.legoshi.lccore.database.models.PlayerCosmetic;
import de.legoshi.lccore.util.ConfigAccessor;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AchievementManager {
    @Inject private Injector injector;
    @Getter private List<Achievement> achievements = new ArrayList<>();
    private static final HashMap<UUID, Set<String>> playerAchievementMap = new HashMap<>();
    private static final HashMap<UUID, Integer> playerPointsMap = new HashMap<>();
    @Inject private DBManager dbManager;

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

            String name = (String) achievementData.get("name");
            String id = name.replace(" ", "");
            int points = (Integer) achievementData.get("points");
            boolean checkOnJoin = (boolean) achievementData.get("checkOnJoin");
            String description = (String) achievementData.get("description");
            AchievementType achievementType = AchievementType.valueOf((String) achievementData.get("type"));
            List<UnlockRequirement> requirements = new ArrayList<>();
            List<Reward> rewards = new ArrayList<>();

            achievement.setName(name);
            achievement.setId(id);
            achievement.setPoints(points);
            achievement.setCheckOnJoin(checkOnJoin);
            achievement.setType(achievementType);
            achievement.setDifficulty(AchievementDifficulty.byPoints(achievement));
            achievement.setDescription(description);

            if (checkOnJoin) {
                List<Map<?, ?>> requirementsData = (List<Map<?, ?>>) achievementData.get("requirements");

                for (Map<?, ?> requirementData : requirementsData) {
                    UnlockRequirementType type = UnlockRequirementType.valueOf((String) requirementData.get("type"));
                    UnlockRequirement req = injector.getInstance(type.mappedClazz);
                    req.init(requirementData);

                    requirements.add(req);
                }
            }

            try {
                List<Map<?, ?>> rewardsData = (List<Map<?, ?>>) achievementData.get("rewards");

                for (Map<?, ?> rewardData : rewardsData) {
                    RewardType type = RewardType.valueOf((String) rewardData.get("type"));
                    Reward reward = injector.getInstance(type.mappedClazz);
                    reward.init(rewardData);
                    rewards.add(reward);
                }
                achievement.setRewards(rewards);
            } catch (Exception e) {
                achievement.setRewards(null);
                return;
            }

            achievement.setRequirements(requirements);

            achievements.add(achievement);
        }
    }

    // code that gets executed after the server started and everything is loaded

    public void loadPlayerAchievements(Player player) {
        playerAchievementMap.put(player.getUniqueId(), getPlayerDataEntries(player));
        playerPointsMap.put(player.getUniqueId(), loadAchievementPoints(player));
    }

    public int getAchievementPoints(Player player) {
        if (playerPointsMap.containsKey(player.getUniqueId())) {
            return playerPointsMap.get(player.getUniqueId());
        }
        return 0;
    }

    public Set<String> getPlayerAchievements(Player player) {
        if (!playerAchievementMap.containsKey(player.getUniqueId())) {
            playerAchievementMap.put(player.getUniqueId(), null);
        }
        return playerAchievementMap.get(player.getUniqueId());
    }


    public boolean hasAchievement(Player player, String achievement) {
        return hasAchievement(player, getAchievementById(achievement));
    }

    public boolean hasAchievement(Player player, Achievement achievement) {
        if (achievement.isCheckOnJoin()) {
            return achievement.isUnlocked(player);
        }
        return playerAchievementMap.get(player.getUniqueId()).contains(achievement.getId());

    }

    public void giveAchievement(Player player, Achievement achievement) {
        String dataEntry = achievement.getId();
        giveAchievement(player, dataEntry);
    }
    public void giveAchievement(Player player, String achievement) {

        UUID playerUUID = player.getUniqueId();
        Set<String> playerEntries = playerAchievementMap.computeIfAbsent(playerUUID, k -> new HashSet<>());

        if (playerEntries.add(achievement)) {
            saveDataEntryToDB(playerUUID, achievement);
        }
    }

    public void removeAchievement(Player player, Achievement achievement) {
        String dataEntry = achievement.getId();
        UUID playerUUID = player.getUniqueId();

        Set<String> playerEntries = playerAchievementMap.get(playerUUID);


        if (playerEntries != null && playerEntries.remove(dataEntry)) {
            removeDataEntryFromDB(playerUUID, dataEntry);
        }

        playerAchievementMap.put(playerUUID, playerEntries);
    }

    public int loadAchievementPoints(Player player) {

        AtomicInteger points = new AtomicInteger();
        points.set(0);
        if (playerAchievementMap.containsKey(player.getUniqueId())) {
            points = new AtomicInteger(getPlayerAchievements(player).stream()
                    .flatMapToInt(achievementString -> achievements.stream()
                            .filter(achievement -> achievement.getId().equals(achievementString))
                            .mapToInt(Achievement::getPoints))
                    .sum());
        }

        AtomicInteger finalPoints = points;
        achievements.stream()
                .filter(Achievement::isCheckOnJoin)
                .forEach(achievement -> {

                    if (achievement.getType() == AchievementType.GRIND) {
                        for (int i = 1; i < 4; i++) {
                            if (achievement.isUnlocked(player, i)) {
                                finalPoints.set(finalPoints.get() + 10 * i - 5);
                            }
                        }
                    } else {
                        if (achievement.isUnlocked(player))  {
                            finalPoints.set(finalPoints.get() + achievement.getPoints());
                        }
                    }
                });

        return finalPoints.get();
    }


    private void saveDataEntryToDB(UUID playerUUID, String dataEntry) {
        PlayerAchievements playerAchievements = new PlayerAchievements(playerUUID.toString(), dataEntry);

        EntityManager em = dbManager.getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.persist(playerAchievements);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public PlayerAchievements findPlayerCosmeticById(Player player) {
        String playerId = player.getUniqueId().toString();
        EntityManager em = dbManager.getEntityManager();
        EntityTransaction et = em.getTransaction();
        PlayerAchievements playerAchievements = null;

        try {
            et.begin();
            playerAchievements = em.find(PlayerAchievements.class, playerId);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return playerAchievements;
    }

    public Set<String> getPlayerDataEntries(Player player) {
        UUID playerUUID = player.getUniqueId();
        String hql = "SELECT pd.dataEntry FROM PlayerAchievements pd WHERE pd.playerUUID = :playerUUID";
        EntityManager em = dbManager.getEntityManager();

        try {
            TypedQuery<String> query = em.createQuery(hql, String.class);
            query.setParameter("playerUUID", playerUUID.toString());

            List<String> resultList = query.getResultList();

            return new HashSet<>(resultList);

        } finally {
            em.close();
        }
    }

    private void removeDataEntryFromDB(UUID playerUUID, String dataEntry) {
        EntityManager em = dbManager.getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();

            int deletedCount = em.createQuery(
                            "DELETE FROM PlayerAchievements pa WHERE pa.playerUUID = :uuid AND pa.dataEntry = :dataEntry")
                    .setParameter("uuid", playerUUID.toString())
                    .setParameter("dataEntry", dataEntry)
                    .executeUpdate();

            et.commit();

            if (deletedCount == 0) {
                System.out.println("Eintrag nicht gefunden oder bereits gelöscht.");
            } else {
                System.out.println("Eintrag erfolgreich gelöscht.");
            }
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    public void updatePoints(Player player) {
        EntityManager em = dbManager.getEntityManager();
        EntityTransaction et = em.getTransaction();
        int newPoints = loadAchievementPoints(player);
        try {
            et.begin();

            PlayerAchievementPoints achievementPoints = em.find(PlayerAchievementPoints.class, player.getUniqueId().toString());

            if (achievementPoints != null) {
                achievementPoints.setPoints(newPoints);
                em.merge(achievementPoints);
            } else {
                achievementPoints = new PlayerAchievementPoints(player.getUniqueId().toString(), newPoints);
                em.persist(achievementPoints);
            }

            et.commit();
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            e.printStackTrace();
        }
    }


    public Achievement getAchievementById(String id) {
        return achievements.stream()
                .filter(achievement -> achievement.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<PlayerAchievementPoints> getTopTenPlayerPoints() {
        EntityManager em = dbManager.getEntityManager();
        String jpql = "SELECT p FROM PlayerAchievementPoints p ORDER BY p.points DESC";
        TypedQuery<PlayerAchievementPoints> query = em.createQuery(jpql, PlayerAchievementPoints.class);
        query.setMaxResults(10);

        return query.getResultList();
    }
}
