package me.sbpro.grassggquests.quests;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import me.sbpro.grassggquests.Quests;
import me.sbpro.grassggquests.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class QuestManager {
    private static final int ACTIVE_QUEST_COUNT = 4;
    private final GrassGGQuests plugin;
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final Random random = new Random();

    public QuestManager(GrassGGQuests plugin) { this.plugin = plugin; }

    public PlayerData getData(UUID uuid) {
        return players.computeIfAbsent(uuid, id -> { PlayerData data = plugin.getDataManager().load(id); fillQuests(data); plugin.getDataManager().save(data); return data; });
    }
    public void loadPlayer(Player player) { getData(player.getUniqueId()); }
    public void savePlayer(Player player) { PlayerData data = players.get(player.getUniqueId()); if (data != null) plugin.getDataManager().save(data); }
    public void saveAll() { plugin.getDataManager().saveAll(players.values()); }
    public void unloadPlayer(Player player) { savePlayer(player); players.remove(player.getUniqueId()); }

    private void fillQuests(PlayerData data) {
        while (data.getActiveQuests().size() < ACTIVE_QUEST_COUNT) {
            QuestDefinition next = getRandomAvailableQuest(data);
            if (next == null) break;
            data.getActiveQuests().add(new ActiveQuest(next.getId(), 0, false));
        }
        if (data.getActiveQuests().size() > ACTIVE_QUEST_COUNT) data.getActiveQuests().subList(ACTIVE_QUEST_COUNT, data.getActiveQuests().size()).clear();
    }

    private QuestDefinition getRandomAvailableQuest(PlayerData data) {
        Set<String> active = new HashSet<>();
        for (ActiveQuest q : data.getActiveQuests()) active.add(q.getQuestId());
        List<QuestDefinition> available = new ArrayList<>();
        for (QuestDefinition q : Quests.MEDIUM_QUESTS) if (!active.contains(q.getId())) available.add(q);
        if (available.isEmpty()) return null;
        return available.get(random.nextInt(available.size()));
    }

    public QuestDefinition getDefinition(String id) { return Quests.QUESTS.get(id); }

    /**
     * Converts a Medium quest into a new Easy quest.
     * This is the only allowed quest transformation and can only happen once
     * because Easy quests are never eligible for this method.
     */
    public boolean easify(Player player, int index) {
        PlayerData data = getData(player.getUniqueId());
        if (index < 0 || index >= data.getActiveQuests().size()) return false;

        ActiveQuest current = data.getActiveQuests().get(index);
        if (current.isEasy()) return false;

        QuestDefinition next = getRandomEasyQuest(data, current.getQuestId());
        if (next == null) return false;

        if (!takeCoins(player, 2)) {
            player.sendMessage(Messages.PREFIX + Messages.NOT_ENOUGH_COINS);
            return false;
        }

        data.getActiveQuests().set(index, new ActiveQuest(next.getId(), 0, true));
        plugin.getDataManager().save(data);
        plugin.getBossBars().hide(player);
        player.sendMessage(Messages.PREFIX + Messages.EASY_SUCCESS);
        return true;
    }

    private boolean takeCoins(Player player, int amount) {
        return plugin.getCoinManager().takeCoins(player.getUniqueId(), amount);
    }

    private QuestDefinition getRandomEasyQuest(PlayerData data, String excluded) {
        Set<String> active = new HashSet<>();
        for (ActiveQuest q : data.getActiveQuests()) active.add(q.getQuestId());

        List<QuestDefinition> available = new ArrayList<>();
        for (QuestDefinition q : Quests.EASY_QUESTS) {
            if (!active.contains(q.getId()) && !q.getId().equalsIgnoreCase(excluded)) {
                available.add(q);
            }
        }

        if (available.isEmpty()) return null;
        return available.get(random.nextInt(available.size()));
    }

    public boolean giveQuest(Player target, String questId, boolean notify) {
        PlayerData data = getData(target.getUniqueId());
        QuestDefinition definition = Quests.QUESTS.get(questId);
        if (definition == null || data.getActiveQuests().size() >= ACTIVE_QUEST_COUNT) return false;
        for (ActiveQuest active : data.getActiveQuests()) if (active.getQuestId().equalsIgnoreCase(questId)) return false;
        data.getActiveQuests().add(new ActiveQuest(questId, 0, false));
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

    public void addProgress(Player player, QuestType type, String target, int amount) {
        if (amount <= 0) return;
        PlayerData data = getData(player.getUniqueId());
        List<ActiveQuest> completed = new ArrayList<>();
        for (ActiveQuest active : new ArrayList<>(data.getActiveQuests())) {
            QuestDefinition quest = getDefinition(active.getQuestId());
            if (quest == null || quest.getType() != type || !quest.getTarget().equalsIgnoreCase(target)) continue;
            int newProgress = Math.min(quest.getRequiredAmount(), active.getProgress() + amount);
            active.setProgress(newProgress);
            if (newProgress >= quest.getRequiredAmount()) completed.add(active);
            else plugin.getBossBars().showProgress(player, quest, newProgress);
        }
        for (ActiveQuest active : completed) completeQuest(player, data, active);
        plugin.getDataManager().save(data);
    }

    private void completeQuest(Player player, PlayerData data, ActiveQuest active) {
        QuestDefinition quest = getDefinition(active.getQuestId());
        if (quest == null || active.getProgress() < quest.getRequiredAmount()) return;
        int reward = active.isEasy() ? 5 : 10;
        data.getActiveQuests().remove(active);
        plugin.getBossBars().hide(player);
        plugin.getCoinManager().addCoins(player.getUniqueId(), reward);
        player.sendTitle(Messages.QUEST_COMPLETION_TITLE.replace("%quest%", quest.getTitle()), Messages.QUEST_COMPLETION_SUBTITLE.replace("%reward%", String.valueOf(reward)), Messages.QUEST_COMPLETION_FADE_IN, Messages.QUEST_COMPLETION_STAY, Messages.QUEST_COMPLETION_FADE_OUT);
        player.sendMessage(Messages.QUEST_COMPLETION_BOX_TOP);
        player.sendMessage(Messages.QUEST_COMPLETION_BOX_MESSAGE.replace("%quest%", quest.getTitle()).replace("%reward%", String.valueOf(reward)));
        player.sendMessage(Messages.QUEST_COMPLETION_BOX_BOTTOM);
        fillQuests(data);
    }

    public void refreshAfterConfigChange() { for (PlayerData data : players.values()) fillQuests(data); }
}
