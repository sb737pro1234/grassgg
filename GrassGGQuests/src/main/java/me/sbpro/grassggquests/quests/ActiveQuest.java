package me.sbpro.grassggquests.quests;

public class ActiveQuest {
    private final String questId;
    private int progress;

    public ActiveQuest(String questId, int progress) {
        this.questId = questId;
        this.progress = progress;
    }

    public String getQuestId() { return questId; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}
