package me.sbpro.grassggquests.data;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.quests.ActiveQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class DataManager {
    private final GrassGGQuests plugin;
    private final File file;
    private final YamlConfiguration data;

    public DataManager(GrassGGQuests plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "players.yml");
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerData load(UUID uuid) {
        PlayerData playerData = new PlayerData(uuid);
        String path = "players." + uuid;
        playerData.setLevel(data.getInt(path + ".level", 1));
        playerData.setXp(data.getInt(path + ".xp", 0));
        playerData.setQuestPoints(data.getInt(path + ".quest-points", 0));

        ConfigurationSection section = data.getConfigurationSection(path + ".active-quests");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String questId = section.getString(key + ".id");
                if (questId == null) continue;
                int progress = section.getInt(key + ".progress", 0);
                playerData.getActiveQuests().add(new ActiveQuest(questId, progress));
            }
        }
        return playerData;
    }

    public void save(PlayerData playerData) {
        String path = "players." + playerData.getUuid();
        data.set(path, null);
        data.set(path + ".level", playerData.getLevel());
        data.set(path + ".xp", playerData.getXp());
        data.set(path + ".quest-points", playerData.getQuestPoints());

        int index = 0;
        for (ActiveQuest quest : playerData.getActiveQuests()) {
            data.set(path + ".active-quests." + index + ".id", quest.getQuestId());
            data.set(path + ".active-quests." + index + ".progress", quest.getProgress());
            index++;
        }

        saveFile();
    }

    public void delete(UUID uuid) {
        data.set("players." + uuid, null);
        saveFile();
    }

    public void saveAll(Iterable<PlayerData> players) {
        for (PlayerData player : players) {
            String path = "players." + player.getUuid();
            data.set(path, null);
            data.set(path + ".level", player.getLevel());
            data.set(path + ".xp", player.getXp());
            data.set(path + ".quest-points", player.getQuestPoints());
            int index = 0;
            for (ActiveQuest quest : player.getActiveQuests()) {
                data.set(path + ".active-quests." + index + ".id", quest.getQuestId());
                data.set(path + ".active-quests." + index + ".progress", quest.getProgress());
                index++;
            }
        }
        saveFile();
    }

    private void saveFile() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save players.yml: " + e.getMessage());
        }
    }
}
