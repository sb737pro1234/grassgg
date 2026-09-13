package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.missions.PlayerMission;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMissionManager {

    private final GrassGGMissions plugin;
    private final PlayerDataManager playerDataManager;

    private final Map<UUID, PlayerMissionData> playerData = new HashMap<>();

    public PlayerMissionManager(GrassGGMissions plugin) {
        this.plugin = plugin;
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    public PlayerMissionData getPlayerData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, id -> new PlayerMissionData());
    }

    public boolean hasPlayer(UUID uuid) {
        return playerData.containsKey(uuid);
    }

    public void removePlayer(UUID uuid) {
        playerData.remove(uuid);
    }

    public Map<UUID, PlayerMissionData> getLoadedPlayers() {
        return playerData;
    }

    public void setPlayerData(UUID uuid, PlayerMissionData data) {
        playerData.put(uuid, data);
    }

    public void savePlayer(UUID uuid) {

        PlayerMissionData data = playerData.get(uuid);

        if (data == null) {
            return;
        }

        playerDataManager.savePlayer(uuid, data);
    }

    public void loadPlayer(UUID uuid) {

        PlayerMissionData data = playerDataManager.loadPlayer(uuid);

        if (data != null) {
            playerData.put(uuid, data);
        }

    }

    public PlayerMission getMission(UUID uuid, int slot) {
        return getPlayerData(uuid).getMissions().get(slot);
    }

    public void setMission(UUID uuid, int slot, PlayerMission mission) {
        getPlayerData(uuid).getMissions().put(slot, mission);
    }

    public void clearMissions(UUID uuid) {
        getPlayerData(uuid).getMissions().clear();
    }


    public void completeMission(UUID uuid, int slot, Player player) {

        PlayerMission mission = getMission(uuid, slot);

        if (mission == null) {
            return;
        }

        me.sbpro.grassggmissions.rewards.RewardManager.reward(player, mission);

        savePlayer(uuid);

    }

    public PlayerMissionData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

}