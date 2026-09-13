package me.sbpro.grassggmissions.managers;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.missions.MissionType;
import me.sbpro.grassggmissions.missions.PlayerMission;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import me.sbpro.grassggmissions.rewards.RewardManager;
import me.sbpro.grassggmissions.menus.DailyMissionsMenu;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import me.sbpro.grassggmissions.managers.BossBarManager;

public class ProgressManager {

    private ProgressManager() {
    }

    public static void addProgress(Player player, PlayerMission mission, int amount) {

        if (mission == null) {
            return;
        }

        if (mission.isCompleted()) {
            return;
        }

        boolean wasCompleted = mission.isCompleted();

        mission.addProgress(amount);
        BossBarManager.show(player, mission);

        if (!wasCompleted && mission.isCompleted()) {

            RewardManager.reward(player, mission);

        }

        GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .savePlayer(player.getUniqueId());

        if (player.getOpenInventory().getTopInventory().getSize() == 54
                && player.getOpenInventory().getTitle().equals("§2§lDaily Missions")) {

            DailyMissionsMenu.open(player);


        }
    }
    public static void handleBlockBreak(Player player, Material material) {
        handleProgress(player, MissionType.BREAK_BLOCK, material);
    }

    private static void handleProgress(Player player,
                                       MissionType type,
                                       Object target) {
        PlayerMissionData data = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId());

        for (PlayerMission mission : data.getMissions().values()) {

            if (mission.isCompleted()) {
                continue;
            }

            if (mission.getMission().getType() != type) {
                continue;
            }

            switch (type) {

                case BREAK_BLOCK -> {

                    if (!mission.getMission().getMaterials().contains(target)) {
                        continue;
                    }

                }

                case CRAFT_ITEM -> {

                    if (!mission.getMission().getMaterials().contains(target)) {
                        continue;
                    }

                }

                case KILL_ENTITY -> {

                    if (mission.getMission().getEntityType() != target) {
                        continue;
                    }

                }

                case PLANT_CROP, HARVEST_CROP, STRIP_LOG -> {

                    if (!mission.getMission().getMaterials().contains(target)) {
                        continue;
                    }

                }

                case BREED_ANIMAL -> {

                    if (mission.getMission().getEntityType() != target) {
                        continue;
                    }

                }

                case CATCH_FISH -> {
                    // No extra check required
                }

            }

            addProgress(player, mission, 1);

        }

    }
    public static void handleCraft(Player player, Material material, int amount) {

        PlayerMissionData data = GrassGGMissions.getInstance()
                .getPlayerMissionManager()
                .getPlayerData(player.getUniqueId());

        for (PlayerMission mission : data.getMissions().values()) {

            if (mission.isCompleted()) {
                continue;
            }

            if (mission.getMission().getType() != MissionType.CRAFT_ITEM) {
                continue;
            }

            if (!mission.getMission().getMaterials().contains(material)) {
                continue;
            }

            addProgress(player, mission, amount);

        }

    }

    public static void handleMobKill(Player player, EntityType entity) {
        handleProgress(player, MissionType.KILL_ENTITY, entity);
    }

    public static void handlePlant(Player player, Material material) {
        handleProgress(player, MissionType.PLANT_CROP, material);
    }

    public static void handleHarvest(Player player, Material material) {
        handleProgress(player, MissionType.HARVEST_CROP, material);
    }

    public static void handleStrip(Player player, Material material) {
        handleProgress(player, MissionType.STRIP_LOG, material);
    }

    public static void handleBreed(Player player, EntityType entity) {
        handleProgress(player, MissionType.BREED_ANIMAL, entity);
    }

    public static void handleFish(Player player) {
        handleProgress(player, MissionType.CATCH_FISH, null);
    }
}