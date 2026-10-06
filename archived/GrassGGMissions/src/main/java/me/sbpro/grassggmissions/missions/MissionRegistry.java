package me.sbpro.grassggmissions.missions;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MissionRegistry {

    private static final List<Mission> MISSIONS = new ArrayList<>();

    public static void registerMissions() {

        MISSIONS.clear();


        /*
         * =========================
         * Mining
         * =========================
         */

        register(new Mission(
                "BREAK_STONE",
                "Break 32 Stone",
                MissionCategory.MINING,
                MissionFamily.BREAK_STONE,
                MissionType.BREAK_BLOCK,
                Set.of(Material.STONE),
                null,
                32
                ));

        register(new Mission(
                "MINE_COAL",
                "Mine 16 Coal Ore",
                MissionCategory.MINING,
                MissionFamily.MINE_COAL,
                MissionType.BREAK_BLOCK,
                Set.of(
                        Material.COAL_ORE,
                        Material.DEEPSLATE_COAL_ORE
                ),
                null,
                16
                ));

        register(new Mission(
                "MINE_IRON",
                "Mine 12 Iron Ore",
                MissionCategory.MINING,
                MissionFamily.MINE_IRON,
                MissionType.BREAK_BLOCK,
                Set.of(
                        Material.IRON_ORE,
                        Material.DEEPSLATE_IRON_ORE
                ),
                null,
                12
                ));

        register(new Mission(
                "MINE_DIAMONDS",
                "Mine 4 Diamonds",
                MissionCategory.MINING,
                MissionFamily.MINE_DIAMOND,
                MissionType.BREAK_BLOCK,
                Set.of(
                        Material.DIAMOND_ORE,
                        Material.DEEPSLATE_DIAMOND_ORE
                ),
                null,
                4
                ));


        /*
         * =========================
         * Woodcutting
         * =========================
         */

        register(new Mission(
                "BREAK_LOGS",
                "Break 32 Logs",
                MissionCategory.WOODCUTTING,
                MissionFamily.BREAK_LOGS,
                MissionType.BREAK_BLOCK,
                Set.of(
                        Material.OAK_LOG,
                        Material.SPRUCE_LOG,
                        Material.BIRCH_LOG,
                        Material.JUNGLE_LOG,
                        Material.ACACIA_LOG,
                        Material.DARK_OAK_LOG,
                        Material.MANGROVE_LOG,
                        Material.CHERRY_LOG,
                        Material.PALE_OAK_LOG,
                        Material.CRIMSON_STEM,
                        Material.WARPED_STEM
                ),
                null,
                32
                ));

        register(new Mission(
                "STRIP_LOGS",
                "Strip 16 Logs",
                MissionCategory.WOODCUTTING,
                MissionFamily.STRIP_LOGS,
                MissionType.STRIP_LOG,
                Set.of(
                        Material.OAK_LOG,
                        Material.SPRUCE_LOG,
                        Material.BIRCH_LOG,
                        Material.JUNGLE_LOG,
                        Material.ACACIA_LOG,
                        Material.DARK_OAK_LOG,
                        Material.MANGROVE_LOG,
                        Material.CHERRY_LOG,
                        Material.PALE_OAK_LOG
                ),
                null,
                16
                ));

        register(new Mission(
                "CRAFT_CHESTS",
                "Craft 8 Chests",
                MissionCategory.WOODCUTTING,
                MissionFamily.CRAFT,
                MissionType.CRAFT_ITEM,
                Set.of(Material.CHEST),
                null,
                8
                ));


        /*
         * =========================
         * Combat
         * =========================
         */

        register(new Mission(
                "KILL_ZOMBIES",
                "Kill 20 Zombies",
                MissionCategory.COMBAT,
                MissionFamily.KILL_MOBS,
                MissionType.KILL_ENTITY,
                null,
                EntityType.ZOMBIE,
                20
                ));

        register(new Mission(
                "KILL_SKELETONS",
                "Kill 15 Skeletons",
                MissionCategory.COMBAT,
                MissionFamily.KILL_MOBS,
                MissionType.KILL_ENTITY,
                null,
                EntityType.SKELETON,
                15
                ));

        register(new Mission(
                "KILL_CREEPERS",
                "Kill 5 Creepers",
                MissionCategory.COMBAT,
                MissionFamily.KILL_MOBS,
                MissionType.KILL_ENTITY,
                null,
                EntityType.CREEPER,
                5
                ));


        /*
         * =========================
         * Farming
         * =========================
         */

        register(new Mission(
                "HARVEST_WHEAT",
                "Harvest 32 Wheat",
                MissionCategory.FARMING,
                MissionFamily.HARVEST_CROPS,
                MissionType.HARVEST_CROP,
                Set.of(Material.WHEAT_SEEDS),
                null,
                32
                ));

        register(new Mission(
                "PLANT_SEEDS",
                "Plant 32 Seeds",
                MissionCategory.FARMING,
                MissionFamily.PLANT_CROPS,
                MissionType.PLANT_CROP,
                Set.of(Material.WHEAT),
                null,
                32
                ));

        register(new Mission(
                "BREED_COWS",
                "Breed 8 Cows",
                MissionCategory.FARMING,
                MissionFamily.BREED_ANIMALS,
                MissionType.BREED_ANIMAL,
                null,
                EntityType.COW,
                8
                ));


        /*
         * =========================
         * Fishing
         * =========================
         */

        register(new Mission(
                "CATCH_FISH",
                "Catch 8 Fish",
                MissionCategory.FISHING,
                MissionFamily.FISH,
                MissionType.CATCH_FISH,
                null,
                null,
                8
                ));


        /*
         * =========================
         * Crafting
         * =========================
         */

        register(new Mission(
                "CRAFT_FURNACES",
                "Craft 8 Furnaces",
                MissionCategory.CRAFTING,
                MissionFamily.CRAFT,
                MissionType.CRAFT_ITEM,
                Set.of(Material.FURNACE),
                null,
                8
                ));

        register(new Mission(
                "CRAFT_TORCHES",
                "Craft 8 Torches",
                MissionCategory.CRAFTING,
                MissionFamily.CRAFT,
                MissionType.CRAFT_ITEM,
                Set.of(Material.TORCH),
                null,
                8
                ));

        register(new Mission(
                "CRAFT_TABLES",
                "Craft 16 Crafting Tables",
                MissionCategory.CRAFTING,
                MissionFamily.CRAFT,
                MissionType.CRAFT_ITEM,
                Set.of(Material.CRAFTING_TABLE),
                null,
                16
                ));

    }


    private static void register(Mission mission) {
        MISSIONS.add(mission);
    }


    public static List<Mission> getMissions() {
        return Collections.unmodifiableList(MISSIONS);
    }


    public static Mission getMission(String id) {

        for (Mission mission : MISSIONS) {

            if (mission.getId().equalsIgnoreCase(id)) {
                return mission;
            }

        }

        return null;
    }

}