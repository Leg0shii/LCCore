package de.legoshi.lccore.manager;

import de.legoshi.lccore.achievements.Achievement;
import de.legoshi.lccore.achievements.AchievementDifficulty;
import de.legoshi.lccore.achievements.AchievementType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AchievementManager {

    private static AchievementManager instance;
    private FileConfiguration config;
    private final Map<String, Achievement> achievementMap = new HashMap<>();

    public AchievementManager() { }

    public static AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    public void loadConfig(JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "achievements.yml");

        if (!configFile.exists()) {
            plugin.saveResource("achievements.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(configFile);
        loadAchievements();
    }

    @SuppressWarnings("unchecked")
    public void loadAchievements() {
        List<?> rawAchievements = config.getList("achievements");

        if (rawAchievements == null) {
            System.out.println("No achievements found in the configuration file.");
            return;
        }

        for (Object item : rawAchievements) {
            if (item instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) item;
                Map<String, Object> achievementData = new HashMap<>();

                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof String) {
                        achievementData.put((String) entry.getKey(), entry.getValue());
                    }
                }

                try {
                    String id = (String) achievementData.get("id");
                    String name = (String) achievementData.get("name");
                    String description = (String) achievementData.get("description");
                    int points = (int) achievementData.get("points");
                    String typeString = (String) achievementData.get("type");
                    AchievementType type = AchievementType.valueOf(typeString);
                    String diffString = (String) achievementData.get("difficulty");
                    AchievementDifficulty difficulty = AchievementDifficulty.valueOf(diffString);

                    Achievement achievement = new Achievement(type, id, name, description, points, difficulty);
                    achievementMap.put(id, achievement);
                    System.out.println("Achievement loaded: " + id);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing achievement data: " + e.getMessage());
                }
            } else {
                System.out.println("Unexpected data format in achievements list.");
            }
        }

        System.out.println("Total achievements loaded: " + achievementMap.size());
    }

    public List<Achievement> getAllAchievements() {
        List<Achievement> achievements = new ArrayList<>(achievementMap.values());
        System.out.println("Achievements retrieved: " + achievements.size());
        return achievements;
    }
}
