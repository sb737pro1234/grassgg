package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.missions.MissionRegistry;
import me.sbpro.grassggmissions.missions.PlayerMission;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final GrassGGMissions plugin;

    private File playerFile;
    private FileConfiguration playerData;

    public PlayerDataManager(GrassGGMissions plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {

        File folder = new File(plugin.getDataFolder(), "dailymissions/data");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        playerFile = new File(folder, "players.yml");

        if (!playerFile.exists()) {

            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        playerData = YamlConfiguration.loadConfiguration(playerFile);

    }

    public FileConfiguration getConfig() {
        return playerData;
    }

    public void save() {

        try {
            playerData.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reload() {
        playerData = YamlConfiguration.loadConfiguration(playerFile);
    }

    public void savePlayer(UUID uuid, PlayerMissionData data) {

        String path = "players." + uuid;

        playerData.set(path + ".last-reset", data.getLastReset());

        playerData.set(path + ".missions", null);

        for (Map.Entry<Integer, PlayerMission> entry : data.getMissions().entrySet()) {

            String missionPath = path + ".missions." + entry.getKey();

            PlayerMission mission = entry.getValue();

            playerData.set(missionPath + ".id", mission.getMission().getId());
            playerData.set(missionPath + ".progress", mission.getProgress());
            playerData.set(missionPath + ".completed", mission.isCompleted());

        }

        save();

    }

    public PlayerMissionData loadPlayer(UUID uuid) {

        String path = "players." + uuid;

        if (!playerData.contains(path)) {
            return null;
        }

        PlayerMissionData data = new PlayerMissionData();

        data.setLastReset(
                playerData.getLong(path + ".last-reset")
        );


        if (playerData.isConfigurationSection(path + ".missions")) {

            for (String key : playerData.getConfigurationSection(path + ".missions").getKeys(false)) {

                String missionPath = path + ".missions." + key;

                String missionId = playerData.getString(missionPath + ".id");

                if (missionId == null) {
                    continue;
                }

                var mission = MissionRegistry.getMission(missionId);

                if (mission == null) {
                    continue;
                }

                PlayerMission playerMission = new PlayerMission(
                        mission,
                        playerData.getInt(missionPath + ".progress"),
                        playerData.getBoolean(missionPath + ".completed")
                );

                data.getMissions().put(
                        Integer.parseInt(key),
                        playerMission
                );

            }

        }

        return data;

    }

}