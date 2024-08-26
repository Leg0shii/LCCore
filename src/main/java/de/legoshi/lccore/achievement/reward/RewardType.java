package de.legoshi.lccore.achievement.reward;

import java.util.Arrays;
import java.util.List;

public enum RewardType {
    TAG(TagReward.class, Arrays.asList("tagId"));

    public final Class<? extends Reward> mappedClazz;
    public final List<String> keys;

    RewardType(Class<? extends Reward> mappedClazz, List<String> keys) {
        this.mappedClazz = mappedClazz;
        this.keys = keys;
    }
}
