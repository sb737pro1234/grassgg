package me.sbpro.grassggquests.data;

import me.sbpro.grassggquests.quests.ActiveQuest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private final List<ActiveQuest> activeQuests = new ArrayList<>();

    public PlayerData(UUID uuid) { this.uuid = uuid; }
    public UUID getUuid() { return uuid; }
    public List<ActiveQuest> getActiveQuests() { return activeQuests; }
}
