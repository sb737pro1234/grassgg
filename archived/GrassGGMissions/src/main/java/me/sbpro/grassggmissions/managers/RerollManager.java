package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.missions.Mission;
import me.sbpro.grassggmissions.missions.MissionRegistry;
import me.sbpro.grassggmissions.missions.PlayerMission;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RerollManager {

    private static final double COST = 35000;

    private static final Random RANDOM = new Random();

    private RerollManager() {
    }

    public static void reroll(Player player, int missionSlot) {

        PlayerMissionData data = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId());

        PlayerMission currentMission = data.getMissions().get(missionSlot);

        if (currentMission == null) {
            return;
        }

        if (currentMission.isCompleted()) {

            player.sendMessage("§cYou cannot reroll a completed mission.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);

            return;
        }

        if (!EconomyManager.has(player, COST)) {

            player.sendMessage("§cYou need £35,000 to reroll this mission.");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1F, 1F);

            return;
        }

        List<Mission> available = new ArrayList<>();

        for (Mission mission : MissionRegistry.getMissions()) {

            boolean duplicate = false;

            for (PlayerMission existing : data.getMissions().values()) {

                if (existing == null) {
                    continue;
                }

                if (existing.getMission().getId().equalsIgnoreCase(mission.getId())) {
                    duplicate = true;
                    break;
                }

            }

            if (!duplicate) {
                available.add(mission);
            }

        }

        if (available.isEmpty()) {

            player.sendMessage("§cThere are no missions available to reroll.");

            return;

        }

        EconomyManager.withdraw(player, COST);

        Mission newMission = available.get(
                RANDOM.nextInt(available.size())
        );

        data.getMissions().put(
                missionSlot,
                new PlayerMission(newMission)
        );

        GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .savePlayer(player.getUniqueId());

        player.sendMessage("§aMission rerolled!");

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1F,
                1F
        );

    }

}