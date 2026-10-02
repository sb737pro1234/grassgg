package me.sbpro.grassggquests.quests;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import me.sbpro.grassggquests.Quests;
import me.sbpro.grassggquests.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class QuestManager {
    private final GrassGGQuests plugin;
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final Random random = new Random();

    public QuestManager(GrassGGQuests plugin) {
        this.plugin = plugin;
    }

    public PlayerData getData(UUID uuid) {
        return players.computeIfAbsent(uuid, id -> {
            PlayerData data = plugin.getDataManager().load(id);
            fillQuests(data);
            plugin.getDataManager().save(data);
            return data;
        });
    }

    public void loadPlayer(Player player) {
        getData(player.getUniqueId());
    }

    public void savePlayer(Player player) {
        PlayerData data = players.get(player.getUniqueId());
        if (data != null) plugin.getDataManager().save(data);
    }

    public void saveAll() {
        plugin.getDataManager().saveAll(players.values());
    }

    public void unloadPlayer(Player player) {
        savePlayer(player);
        players.remove(player.getUniqueId());
    }

    private void fillQuests(PlayerData data) {
        while (data.getActiveQuests().size() < 5) {
            QuestDefinition next = getRandomAvailableQuest(data);
            if (next == null) break;
            data.getActiveQuests().add(new ActiveQuest(next.getId(), 0));
        }
    }

    private QuestDefinition getRandomAvailableQuest(PlayerData data) {
        Set<String> active = new HashSet<>();
        for (ActiveQuest activeQuest : data.getActiveQuests()) active.add(activeQuest.getQuestId());
        List<QuestDefinition> available = new ArrayList<>();
        for (QuestDefinition quest : Quests.QUESTS.values()) {
            if (!active.contains(quest.getId())) available.add(quest);
        }
        if (available.isEmpty()) return null;
        return available.get(random.nextInt(available.size()));
    }

    public QuestDefinition getDefinition(String id) {
        return Quests.QUESTS.get(id);
    }

    public boolean giveQuest(Player target, String questId, boolean notify) {
        PlayerData data = getData(target.getUniqueId());
        QuestDefinition definition = Quests.QUESTS.get(questId);
        if (definition == null || data.getActiveQuests().size() >= 5) return false;
        for (ActiveQuest active : data.getActiveQuests()) {
            if (active.getQuestId().equalsIgnoreCase(questId)) return false;
        }
        data.getActiveQuests().add(new ActiveQuest(questId, 0));
        plugin.getDataManager().save(data);
        if (notify) target.sendMessage(Messages.PREFIX + Messages.QUEST_GIVEN_TO_YOU.replace("%quest%", definition.getTitle()));
        return true;
    }

    public void reset(Player target) {
        PlayerData data = new PlayerData(target.getUniqueId());
        fillQuests(data);
        players.put(target.getUniqueId(), data);
        plugin.getDataManager().save(data);
    }

    public void setLevel(Player target, int level) {
        PlayerData data = getData(target.getUniqueId());
        data.setLevel(Math.max(1, level));
        data.setXp(0);
        data.setQuestPoints(Math.max(0, level - 1));
        plugin.getDataManager().save(data);
    }

    public void addProgress(Player player, QuestType type, String target, int amount) {
        if (amount <= 0) return;

        PlayerData data = getData(player.getUniqueId());
        List<ActiveQuest> completed = new ArrayList<>();

        // Only matching active quests receive progress. Completion is handled separately
        // and can NEVER happen merely because progress was made.
        for (ActiveQuest active : new ArrayList<>(data.getActiveQuests())) {
            QuestDefinition quest = getDefinition(active.getQuestId());
            if (quest == null || quest.getType() != type || !quest.getTarget().equalsIgnoreCase(target)) {
                continue;
            }

            int required = quest.getRequiredAmount();
            int newProgress = Math.min(required, active.getProgress() + amount);
            active.setProgress(newProgress);

            if (required > 0 && newProgress >= required) {
                completed.add(active);
            } else {
                // This is the normal progress path: show/update the bossbar only.
                plugin.getBossBars().showProgress(player, quest, newProgress);
            }
        }

        // Completion messages/title are ONLY sent from here, after the requirement is reached.
        for (ActiveQuest active : completed) {
            completeQuest(player, data, active);
        }

        plugin.getDataManager().save(data);
    }

    private void completeQuest(Player player, PlayerData data, ActiveQuest active) {
        QuestDefinition quest = getDefinition(active.getQuestId());
        if (quest == null || active.getProgress() < quest.getRequiredAmount()) return;
        data.getActiveQuests().remove(active);
        data.setXp(data.getXp() + quest.getXpReward());
        data.setQuestPoints(data.getQuestPoints() + 1);

        plugin.getBossBars().hide(player);

        player.sendTitle(
                Messages.QUEST_COMPLETION_TITLE.replace("%quest%", quest.getTitle()),
                Messages.QUEST_COMPLETION_SUBTITLE.replace("%quest%", quest.getTitle()),
                Messages.QUEST_COMPLETION_FADE_IN,
                Messages.QUEST_COMPLETION_STAY,
                Messages.QUEST_COMPLETION_FADE_OUT);

        player.sendMessage(Messages.QUEST_COMPLETION_BOX_TOP);
        player.sendMessage(Messages.QUEST_COMPLETION_BOX_MESSAGE
                .replace("%quest%", quest.getTitle())
                .replace("%xp%", String.valueOf(quest.getXpReward())));
        player.sendMessage(Messages.QUEST_COMPLETION_BOX_BOTTOM);

        while (canLevelUp(data)) {
            int required = Quests.LEVEL_XP.getOrDefault(data.getLevel(), Integer.MAX_VALUE);
            data.setXp(data.getXp() - required);
            data.setLevel(data.getLevel() + 1);
            data.setQuestPoints(data.getQuestPoints() + 1);
            player.sendMessage(Messages.PREFIX + Messages.LEVEL_UP.replace("%level%", String.valueOf(data.getLevel())));
            player.sendMessage(Messages.PREFIX + Messages.POINT_RECEIVED);
        }

        fillQuests(data);
    }

    private boolean canLevelUp(PlayerData data) {
        Integer required = Quests.LEVEL_XP.get(data.getLevel());
        return required != null && data.getXp() >= required;
    }

    public int getRequiredXp(PlayerData data) {
        return Quests.LEVEL_XP.getOrDefault(data.getLevel(), 0);
    }

    public void refreshAfterConfigChange() {
        for (PlayerData data : players.values()) fillQuests(data);
    }
}
