package me.sbpro.grassggmissions.missions;

public class PlayerMission {

    private final Mission mission;

    private int progress;

    private boolean completed;

    public PlayerMission(Mission mission) {
        this.mission = mission;
        this.progress = 0;
        this.completed = false;
    }

    public Mission getMission() {
        return mission;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void addProgress(int amount) {

        if (completed) {
            return;
        }

        progress += amount;

        if (progress >= mission.getGoal()) {
            progress = mission.getGoal();
            completed = true;
        }

    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public PlayerMission(Mission mission, int progress, boolean completed) {
        this.mission = mission;
        this.progress = progress;
        this.completed = completed;
    }

    public double getProgressPercent() {

        return (double) progress / mission.getGoal();

    }

    public int getProgressPercentage() {

        return (int) Math.round(getProgressPercent() * 100);

    }

    public String getProgressString() {

        return progress + "/" + mission.getGoal();

    }

    public boolean matches(MissionType type) {
        return mission.getType() == type;
    }

}