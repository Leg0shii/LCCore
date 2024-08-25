package de.legoshi.lccore.achievements;

public class Achievement {
    private final AchievementType type;
    private final String id;
    private final String name;
    private final String description;
    private final int points;
    private final AchievementDifficulty difficulty;

    public Achievement(AchievementType type, String id, String name, String description, int points, AchievementDifficulty difficulty) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
        this.difficulty = difficulty;
    }

    // Getters for each field
    public AchievementType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPoints() {
        return points;
    }

    public AchievementDifficulty getDifficulty() {
        return difficulty;
    }

}

