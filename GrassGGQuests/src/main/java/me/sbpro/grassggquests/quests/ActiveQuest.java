package me.sbpro.grassggquests.quests;

public class ActiveQuest {
    private final String questId;
    private int progress;
    private boolean easy;

    public ActiveQuest(String questId, int progress) {
        this(questId, progress, false);
    }

    public ActiveQuest(String questId, int progress, boolean easy) {
        this.questId = questId;
        this.progress = progress;
        this.easy = easy;
    }

    public String getQuestId() { return questId; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public boolean isEasy() { return easy; }
    public void setEasy(boolean easy) { this.easy = easy; }
}
