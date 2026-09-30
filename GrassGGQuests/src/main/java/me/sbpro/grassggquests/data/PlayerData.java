package me.sbpro.grassggquests.data;

import me.sbpro.grassggquests.quests.ActiveQuest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private int level = 1;
    private int xp = 0;
    private int questPoints = 0;
    private final List<ActiveQuest> activeQuests = new ArrayList<>();

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = Math.max(0, xp); }
    public int getQuestPoints() { return questPoints; }
    public void setQuestPoints(int questPoints) { this.questPoints = Math.max(0, questPoints); }
    public List<ActiveQuest> getActiveQuests() { return activeQuests; }
}
