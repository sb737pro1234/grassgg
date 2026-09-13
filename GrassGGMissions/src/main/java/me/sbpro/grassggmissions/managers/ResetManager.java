package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.missions.Mission;
import me.sbpro.grassggmissions.missions.MissionRegistry;
import me.sbpro.grassggmissions.missions.PlayerMission;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResetManager {

    public static final long RESET_TIME = 24L * 60L * 60L * 1000L;

    private ResetManager() {
    }

    public static void checkReset(Player player) {

        PlayerMissionData data = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId());

        if (data.getMissions().isEmpty()) {
            reset(player);
            return;
        }

        if (System.currentTimeMillis() - data.getLastReset() >= RESET_TIME) {
            reset(player);
        }

    }

    public static void reset(Player player) {

        PlayerMissionData data = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId());

        data.getMissions().clear();

        List<Mission> missions = new ArrayList<>(MissionRegistry.getMissions());

        Collections.shuffle(missions);

        for (int i = 0; i < 4; i++) {

            data.getMissions().put(
                    i + 1,
                    new PlayerMission(missions.get(i))
            );

        }

        data.setLastReset(System.currentTimeMillis());

        GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .savePlayer(player.getUniqueId());

    }
    public static long getTimeUntilReset(Player player) {

        long lastReset = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId())
                .getLastReset();

        long remaining = RESET_TIME - (System.currentTimeMillis() - lastReset);

        return Math.max(0, remaining);

    }
}