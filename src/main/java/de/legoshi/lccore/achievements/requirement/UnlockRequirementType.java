package de.legoshi.lccore.achievements.requirement;

import java.util.Arrays;
import java.util.List;

public enum UnlockRequirementType {
    TAG_UNLOCKED_AMOUNT(TagUnlockedAmountRequirement.class, Arrays.asList("requiredTagCount")),
    MAP_LIST_COMPLETION(AllMapsCompletionRequirement.class, Arrays.asList("requiredMaps")),
    PLAYTIME_AMOUNT(PlaytimeRequirement.class, Arrays.asList("requiredPlaytime")),
    JUMP_AMOUNT(JumpCountRequirement.class, Arrays.asList("requiredJumpCount")),
    PP_AMOUNT(PPAmountRequirement.class, Arrays.asList("requiredParkourPoints"));

    public final Class<? extends UnlockRequirement> mappedClazz;
    public final List<String> keys;

    UnlockRequirementType(Class<? extends UnlockRequirement> mappedClazz, List<String> keys) {
        this.mappedClazz = mappedClazz;
        this.keys = keys;
    }
}
