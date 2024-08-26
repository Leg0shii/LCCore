package de.legoshi.lccore.achievement.requirement;

import java.util.Arrays;
import java.util.List;

public enum UnlockRequirementType {
    TAG_UNLOCKED_AMOUNT(TagUnlockedAmountRequirement.class, Arrays.asList("requiredTagCount")),
    MAP_LIST_COMPLETION(AllMapsCompletionRequirement.class, Arrays.asList("requiredMaps"));

    public final Class<? extends UnlockRequirement> mappedClazz;
    public final List<String> keys;

    UnlockRequirementType(Class<? extends UnlockRequirement> mappedClazz, List<String> keys) {
        this.mappedClazz = mappedClazz;
        this.keys = keys;
    }
}
