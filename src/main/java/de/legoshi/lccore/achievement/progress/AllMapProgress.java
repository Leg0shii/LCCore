package de.legoshi.lccore.achievement.progress;

import java.util.List;

public class AllMapProgress implements Progress {
    private List<String> incomplete;
    private List<String> complete;

    public AllMapProgress(List<String> incomplete, List<String> complete) {
        this.incomplete = incomplete;
        this.complete = complete;
    }


    @Override
    public String display() {
        // TODO: implement some sort of formatted display for GUI
        return "";
    }
}
