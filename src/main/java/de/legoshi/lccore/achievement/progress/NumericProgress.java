package de.legoshi.lccore.achievement.progress;

public class NumericProgress implements Progress {
    private int current;
    private int goal;

    public NumericProgress(int current, int goal) {
        this.current = current;
        this.goal = goal;
    }

    public int getCompletionPercentage() {
        return (int)((double) current / goal * 100);
    }

    @Override
    public String display() {
        //current + " / " + goal + " (" + getCompletionPercentage() + "%)";
        // TODO: implement some sort of formatted display for GUI
        return "";
    }
}
