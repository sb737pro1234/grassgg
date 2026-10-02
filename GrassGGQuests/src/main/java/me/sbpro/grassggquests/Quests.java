package me.sbpro.grassggquests;

import me.sbpro.grassggquests.quests.QuestDefinition;
import me.sbpro.grassggquests.quests.QuestType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ============================================================================
 * QUEST CONFIGURATION
 * ============================================================================
 *
 * This is the main place where you create and edit quests.
 *
 * IMPORTANT:
 * - Do NOT put quest definitions in config.yml.
 * - Add/remove/edit quests in the QUESTS map below.
 * - Quest XP level requirements are in LEVEL_XP at the bottom of this file.
 *
 * Each quest uses this format:
 *
 * QUESTS.put("ID", new QuestDefinition(
 *         "ID",
 *         "Quest Name",
 *         "Quest information shown to the player.",
 *         QuestType.OBJECTIVE_TYPE,
 *         "TARGET",
 *         REQUIRED_AMOUNT,
 *         XP_REWARD));
 *
 * What each value means:
 *
 * 1. ID
 *    A unique internal name for the quest.
 *    This is what commands such as /quests give use.
 *    Example: "craft_bread"
 *
 * 2. Quest Name
 *    The literal name displayed to players.
 *    This should describe the actual task, including the amount where useful.
 *    Example: "Craft 32 Bread"
 *
 * 3. Description
 *    The information/lore shown for the quest in the Active Quests menu.
 *    Example: "Craft 32 Bread."
 *
 * 4. QuestType
 *    The type of action that counts towards the quest.
 *    Available types are defined in QuestType.java.
 *    Examples:
 *      BREAK_BLOCK  - breaking a block
 *      PLACE_BLOCK  - placing a block
 *      KILL_ENTITY  - killing an entity
 *      CRAFT_ITEM   - crafting an item
 *      FISH_ITEM    - catching an item while fishing
 *      EAT_ITEM     - eating an item
 *
 * 5. Target
 *    The Minecraft material/entity that the player must interact with.
 *    Use the Bukkit/Minecraft enum name in CAPITALS.
 *    Examples: "STONE", "BREAD", "ZOMBIE", "OAK_LOG"
 *
 * 6. Required Amount
 *    How many successful actions are required to complete the quest.
 *    Example: 32 means the player must craft/eat/etc. 32 of the target.
 *
 * 7. XP Reward
 *    How much Quest XP the player receives when this quest is completed.
 *
 * ---------------------------------------------------------------------------
 * HOW TO ADD A NEW QUEST
 * ---------------------------------------------------------------------------
 *
 * Copy one of the QUESTS.put(...) entries and change every value you need.
 * Make sure the first ID is unique.
 *
 * Example:
 *
 * QUESTS.put("mine_diamond", new QuestDefinition(
 *         "mine_diamond",
 *         "Mine 10 Diamonds",
 *         "Mine 10 Diamond Ore blocks.",
 *         QuestType.BREAK_BLOCK,
 *         "DIAMOND_ORE",
 *         10,
 *         250));
 *
 * ---------------------------------------------------------------------------
 * LEVEL XP CONFIGURATION
 * ---------------------------------------------------------------------------
 *
 * LEVEL_XP controls how much total Quest XP is needed to move from a level
 * to the next level.
 *
 * Example:
 *   LEVEL_XP.put(1, 250);
 * means Level 1 -> Level 2 requires 250 XP.
 *
 *   LEVEL_XP.put(2, 400);
 * means Level 2 -> Level 3 requires 400 XP.
 *
 * Add more entries if you want more levels.
 * ============================================================================
 */
public final class Quests {
    private Quests() {}

    /** All available quest definitions. */
    public static final Map<String, QuestDefinition> QUESTS = new LinkedHashMap<>();

    /** XP requirements for each level -> next level transition. */
    public static final Map<Integer, Integer> LEVEL_XP = new LinkedHashMap<>();

    static {
// ---------------------------------------------------------------------
// NEW QUESTS
// ---------------------------------------------------------------------

// ----- EASY (~1-5 min) -----
        QUESTS.put("mine_dirt", new QuestDefinition(
                "mine_dirt", "Mine 200 Dirt", "Mine 200 Dirt blocks.",
                QuestType.BREAK_BLOCK, "DIRT", 200, 50));              // fastest block to break

        QUESTS.put("mine_sand", new QuestDefinition(
                "mine_sand", "Mine 150 Sand", "Mine 150 Sand blocks.",
                QuestType.BREAK_BLOCK, "SAND", 150, 65));

        QUESTS.put("mine_gravel", new QuestDefinition(
                "mine_gravel", "Mine 100 Gravel", "Mine 100 Gravel blocks.",
                QuestType.BREAK_BLOCK, "GRAVEL", 100, 60));

        QUESTS.put("place_planks", new QuestDefinition(
                "place_planks", "Place 128 Oak Planks", "Place 128 Oak Planks.",
                QuestType.PLACE_BLOCK, "OAK_PLANKS", 128, 45));        // exploitable, keep low

        QUESTS.put("chop_birch", new QuestDefinition(
                "chop_birch", "Break 100 Birch Logs", "Break 100 Birch Logs.",
                QuestType.BREAK_BLOCK, "BIRCH_LOG", 100, 115));        // birch forests are rarer

        QUESTS.put("chop_spruce", new QuestDefinition(
                "chop_spruce", "Break 100 Spruce Logs", "Break 100 Spruce Logs.",
                QuestType.BREAK_BLOCK, "SPRUCE_LOG", 100, 115));

        QUESTS.put("kill_chickens", new QuestDefinition(
                "kill_chickens", "Kill 20 Chickens", "Kill 20 Chickens.",
                QuestType.KILL_ENTITY, "CHICKEN", 20, 75));

        QUESTS.put("kill_cows", new QuestDefinition(
                "kill_cows", "Kill 20 Cows", "Kill 20 Cows.",
                QuestType.KILL_ENTITY, "COW", 20, 90));

        QUESTS.put("kill_pigs", new QuestDefinition(
                "kill_pigs", "Kill 20 Pigs", "Kill 20 Pigs.",
                QuestType.KILL_ENTITY, "PIG", 20, 90));

// ----- MEDIUM (~8-12 min) -----
        QUESTS.put("mine_coal", new QuestDefinition(
                "mine_coal", "Mine 64 Coal Ore", "Mine 64 Coal Ore.",
                QuestType.BREAK_BLOCK, "COAL_ORE", 64, 175));          // needs exploring

        QUESTS.put("mine_iron", new QuestDefinition(
                "mine_iron", "Mine 48 Iron Ore", "Mine 48 Iron Ore.",
                QuestType.BREAK_BLOCK, "IRON_ORE", 48, 250));

        QUESTS.put("harvest_wheat", new QuestDefinition(
                "harvest_wheat", "Harvest 100 Wheat", "Harvest 100 Wheat.",
                QuestType.BREAK_BLOCK, "WHEAT", 100, 175));            // needs a farm

        QUESTS.put("craft_furnaces", new QuestDefinition(
                "craft_furnaces", "Craft 16 Furnaces", "Craft 16 Furnaces.",
                QuestType.CRAFT_ITEM, "FURNACE", 16, 150));            // 128 cobblestone

        QUESTS.put("craft_cake", new QuestDefinition(
                "craft_cake", "Craft 5 Cakes", "Craft 5 Cakes.",
                QuestType.CRAFT_ITEM, "CAKE", 5, 225));               // milk, eggs, sugar, wheat

        QUESTS.put("eat_cooked_beef", new QuestDefinition(
                "eat_cooked_beef", "Eat 20 Steak", "Eat 20 Cooked Beef.",
                QuestType.EAT_ITEM, "COOKED_BEEF", 20, 200));

        QUESTS.put("fish_salmon", new QuestDefinition(
                "fish_salmon", "Catch 15 Salmon", "Catch 15 Salmon.",
                QuestType.FISH_ITEM, "SALMON", 15, 300));              // rarer than cod

        QUESTS.put("kill_spiders", new QuestDefinition(
                "kill_spiders", "Kill 25 Spiders", "Kill 25 Spiders.",
                QuestType.KILL_ENTITY, "SPIDER", 25, 275));

        QUESTS.put("kill_creepers", new QuestDefinition(
                "kill_creepers", "Kill 20 Creepers", "Kill 20 Creepers.",
                QuestType.KILL_ENTITY, "CREEPER", 20, 300));           // explosive danger bonus

// ----- HARD (~15-20 min) -----
        QUESTS.put("kill_witches", new QuestDefinition(
                "kill_witches", "Kill 10 Witches", "Kill 10 Witches.",
                QuestType.KILL_ENTITY, "WITCH", 10, 450));             // rare spawns

        QUESTS.put("kill_blazes", new QuestDefinition(
                "kill_blazes", "Kill 25 Blazes", "Kill 25 Blazes.",
                QuestType.KILL_ENTITY, "BLAZE", 25, 550));             // Nether fortress

        QUESTS.put("kill_wither_skeletons", new QuestDefinition(
                "kill_wither_skeletons", "Kill 15 Wither Skeletons", "Kill 15 Wither Skeletons.",
                QuestType.KILL_ENTITY, "WITHER_SKELETON", 15, 600));   // Nether + wither effect

        QUESTS.put("mine_emerald", new QuestDefinition(
                "mine_emerald", "Mine 8 Emerald Ore", "Mine 8 Emerald Ore.",
                QuestType.BREAK_BLOCK, "EMERALD_ORE", 8, 500));        // mountain biomes only

        QUESTS.put("mine_debris", new QuestDefinition(
                "mine_debris", "Mine 5 Ancient Debris", "Mine 5 Ancient Debris.",
                QuestType.BREAK_BLOCK, "ANCIENT_DEBRIS", 5, 650));     // rarest, Nether mining

        QUESTS.put("craft_golden_apples", new QuestDefinition(
                "craft_golden_apples", "Craft 5 Golden Apples", "Craft 5 Golden Apples.",
                QuestType.CRAFT_ITEM, "GOLDEN_APPLE", 5, 450));        // 40 gold ingots

        // ---------------------------------------------------------------------
        // QUEST LEVEL XP
        // ---------------------------------------------------------------------
        // The number on the left is the current level.
        // The number on the right is the XP needed to reach the NEXT level.
        //
        // Example: 1 -> 2 requires 250 XP.
        LEVEL_XP.put(1, 150);
        LEVEL_XP.put(2, 250);
        LEVEL_XP.put(3, 350);
        LEVEL_XP.put(4, 500);
        LEVEL_XP.put(5, 700);
        LEVEL_XP.put(6, 900);
        LEVEL_XP.put(7, 1150);
        LEVEL_XP.put(8, 1400);
        LEVEL_XP.put(9, 1700);
        LEVEL_XP.put(10, 2000);
        LEVEL_XP.put(11, 2350);
        LEVEL_XP.put(12, 2700);
        LEVEL_XP.put(13, 3100);
        LEVEL_XP.put(14, 3500);
    }
}
