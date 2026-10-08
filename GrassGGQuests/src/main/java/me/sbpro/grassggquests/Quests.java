package me.sbpro.grassggquests;

import me.sbpro.grassggquests.quests.QuestDefinition;
import me.sbpro.grassggquests.quests.QuestType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Quest definitions used by GrassGGQuests. */
public final class Quests {
    private Quests() {}

    /** All available quest definitions. */
    public static final Map<String, QuestDefinition> QUESTS = new LinkedHashMap<>();

    /** Easy quests used when a Medium quest is Easified. */
    public static final List<QuestDefinition> EASY_QUESTS;

    /** Medium quests used for the four starting quests and replacements after completion. */
    public static final List<QuestDefinition> MEDIUM_QUESTS;

    static {
        // ---------------------------------------------------------------------
        // EASY
        // Approximately 2-5 minutes each
        // ---------------------------------------------------------------------

        add(new QuestDefinition("mine_dirt", "Mine 150 Dirt", "Mine 150 Dirt blocks.",
                QuestType.BREAK_BLOCK, "DIRT", 150));
        add(new QuestDefinition("mine_sand", "Mine 100 Sand", "Mine 100 Sand blocks.",
                QuestType.BREAK_BLOCK, "SAND", 100));
        add(new QuestDefinition("mine_gravel", "Mine 75 Gravel", "Mine 75 Gravel blocks.",
                QuestType.BREAK_BLOCK, "GRAVEL", 75));
        add(new QuestDefinition("mine_cobblestone", "Mine 100 Cobblestone", "Mine 100 Cobblestone blocks.",
                QuestType.BREAK_BLOCK, "COBBLESTONE", 100));
        add(new QuestDefinition("chop_oak", "Break 64 Oak Logs", "Break 64 Oak Logs.",
                QuestType.BREAK_BLOCK, "OAK_LOG", 64));
        add(new QuestDefinition("chop_birch", "Break 64 Birch Logs", "Break 64 Birch Logs.",
                QuestType.BREAK_BLOCK, "BIRCH_LOG", 64));
        add(new QuestDefinition("kill_chickens", "Kill 15 Chickens", "Kill 15 Chickens.",
                QuestType.KILL_ENTITY, "CHICKEN", 15));
        add(new QuestDefinition("kill_cows", "Kill 15 Cows", "Kill 15 Cows.",
                QuestType.KILL_ENTITY, "COW", 15));
        add(new QuestDefinition("kill_pigs", "Kill 15 Pigs", "Kill 15 Pigs.",
                QuestType.KILL_ENTITY, "PIG", 15));
        add(new QuestDefinition("craft_torches", "Craft 32 Torches", "Craft 32 Torches.",
                QuestType.CRAFT_ITEM, "TORCH", 32));
        add(new QuestDefinition("craft_bread", "Craft 8 Bread", "Craft 8 Bread.",
                QuestType.CRAFT_ITEM, "BREAD", 8));
        add(new QuestDefinition("eat_food", "Eat 10 Steak", "Eat 10 Cooked Beef.",
                QuestType.EAT_ITEM, "COOKED_BEEF", 10));

        EASY_QUESTS = List.copyOf(QUESTS.values());

        // ---------------------------------------------------------------------
        // MEDIUM
        // Approximately 8-12 minutes each
        // ---------------------------------------------------------------------

        addMedium(new QuestDefinition("mine_coal", "Mine 48 Coal Ore", "Mine 48 Coal Ore.",
                QuestType.BREAK_BLOCK, "COAL_ORE", 48));
        addMedium(new QuestDefinition("mine_iron", "Mine 32 Iron Ore", "Mine 32 Iron Ore.",
                QuestType.BREAK_BLOCK, "IRON_ORE", 32));
        addMedium(new QuestDefinition("mine_copper", "Mine 48 Copper Ore", "Mine 48 Copper Ore.",
                QuestType.BREAK_BLOCK, "COPPER_ORE", 48));
        addMedium(new QuestDefinition("mine_gold", "Mine 16 Gold Ore", "Mine 16 Gold Ore.",
                QuestType.BREAK_BLOCK, "GOLD_ORE", 16));
        addMedium(new QuestDefinition("harvest_wheat", "Harvest 64 Wheat", "Harvest 64 Wheat.",
                QuestType.BREAK_BLOCK, "WHEAT", 64));
        addMedium(new QuestDefinition("chop_spruce", "Break 128 Spruce Logs", "Break 128 Spruce Logs.",
                QuestType.BREAK_BLOCK, "SPRUCE_LOG", 128));
        addMedium(new QuestDefinition("craft_furnaces", "Craft 8 Furnaces", "Craft 8 Furnaces.",
                QuestType.CRAFT_ITEM, "FURNACE", 8));
        addMedium(new QuestDefinition("craft_cooked_beef", "Craft 32 Steak", "Craft 32 Cooked Beef.",
                QuestType.CRAFT_ITEM, "COOKED_BEEF", 32));
        addMedium(new QuestDefinition("eat_steak", "Eat 20 Steak", "Eat 20 Cooked Beef.",
                QuestType.EAT_ITEM, "COOKED_BEEF", 20));
        addMedium(new QuestDefinition("fish_cod", "Catch 20 Cod", "Catch 20 Cod.",
                QuestType.FISH_ITEM, "COD", 20));
        addMedium(new QuestDefinition("kill_spiders", "Kill 25 Spiders", "Kill 25 Spiders.",
                QuestType.KILL_ENTITY, "SPIDER", 25));
        addMedium(new QuestDefinition("kill_creepers", "Kill 20 Creepers", "Kill 20 Creepers.",
                QuestType.KILL_ENTITY, "CREEPER", 20));

        MEDIUM_QUESTS = List.copyOf(QUESTS.values().stream()
                .skip(EASY_QUESTS.size())
                .toList());
    }

    private static void add(QuestDefinition quest) {
        QUESTS.put(quest.getId(), quest);
    }

    private static void addMedium(QuestDefinition quest) {
        QUESTS.put(quest.getId(), quest);
    }
}
