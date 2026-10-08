package me.sbpro.grassggquests.data;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.quests.ActiveQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {
    private final GrassGGQuests plugin;
    private final File file;
    private final YamlConfiguration data;

    public DataManager(GrassGGQuests plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        file = new File(plugin.getDataFolder(), "players.yml");
        data = YamlConfiguration.loadConfiguration(file);
    }

    public PlayerData load(UUID uuid) {
        PlayerData playerData = new PlayerData(uuid);
        String path = "players." + uuid;
        ConfigurationSection section = data.getConfigurationSection(path + ".active-quests");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String questId = section.getString(key + ".id");
                if (questId == null) continue;
                int progress = section.getInt(key + ".progress", 0);
                boolean easy = section.getBoolean(key + ".easy", false);
                playerData.getActiveQuests().add(new ActiveQuest(questId, progress, easy));
            }
        }
        return playerData;
    }

    public void save(PlayerData playerData) {
        String path = "players." + playerData.getUuid();
        data.set(path, null);
        int index = 0;
        for (ActiveQuest quest : playerData.getActiveQuests()) {
            data.set(path + ".active-quests." + index + ".id", quest.getQuestId());
            data.set(path + ".active-quests." + index + ".progress", quest.getProgress());
            data.set(path + ".active-quests." + index + ".easy", quest.isEasy());
            index++;
        }
        saveFile();
    }

    public void delete(UUID uuid) { data.set("players." + uuid, null); saveFile(); }

    public void saveAll(Iterable<PlayerData> players) {
        for (PlayerData player : players) {
            String path = "players." + player.getUuid();
            data.set(path, null);
            int index = 0;
            for (ActiveQuest quest : player.getActiveQuests()) {
                data.set(path + ".active-quests." + index + ".id", quest.getQuestId());
                data.set(path + ".active-quests." + index + ".progress", quest.getProgress());
                data.set(path + ".active-quests." + index + ".easy", quest.isEasy());
                index++;
            }
        }
        saveFile();
    }

    private void saveFile() {
        try { data.save(file); } catch (IOException e) { plugin.getLogger().severe("Could not save players.yml: " + e.getMessage()); }
    }
}
