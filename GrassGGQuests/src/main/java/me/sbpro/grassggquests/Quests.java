package me.sbpro.grassggquests;

import me.sbpro.grassggquests.quests.QuestDefinition;
import me.sbpro.grassggquests.quests.QuestType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * All quest and Quest XP configuration belongs in this class.
 * Add, remove or edit entries in QUESTS and LEVEL_XP as required.
 */
public final class Quests {
    private Quests() {}

    public static final Map<String, QuestDefinition> QUESTS = new LinkedHashMap<>();
    public static final Map<Integer, Integer> LEVEL_XP = new LinkedHashMap<>();

    static {
        // id, title, information, objective type, target, amount, XP reward
        QUESTS.put("mine_stone", new QuestDefinition(
                "mine_stone", "Mine Stone", "Mine 250 Stone blocks.",
                QuestType.BREAK_BLOCK, "STONE", 250, 100));
        QUESTS.put("chop_oak", new QuestDefinition(
                "chop_oak", "Chop Oak", "Break 100 Oak Logs.",
                QuestType.BREAK_BLOCK, "OAK_LOG", 100, 125));
        QUESTS.put("place_cobblestone", new QuestDefinition(
                "place_cobblestone", "Place Cobblestone", "Place 100 Cobblestone blocks.",
                QuestType.PLACE_BLOCK, "COBBLESTONE", 100, 100));
        QUESTS.put("kill_zombies", new QuestDefinition(
                "kill_zombies", "Zombie Hunter", "Defeat 25 Zombies.",
                QuestType.KILL_ENTITY, "ZOMBIE", 25, 150));
        QUESTS.put("craft_bread", new QuestDefinition(
                "craft_bread", "Bake Bread", "Craft 32 Bread.",
                QuestType.CRAFT_ITEM, "BREAD", 32, 100));
        QUESTS.put("fish_cod", new QuestDefinition(
                "fish_cod", "Catch Cod", "Catch 15 Cod.",
                QuestType.FISH_ITEM, "COD", 15, 125));
        QUESTS.put("eat_baked_potato", new QuestDefinition(
                "eat_baked_potato", "Potato Time", "Eat 20 Baked Potatoes.",
                QuestType.EAT_ITEM, "BAKED_POTATO", 20, 75));

        // XP required to move FROM this level TO the next level.
        LEVEL_XP.put(1, 250);
        LEVEL_XP.put(2, 400);
        LEVEL_XP.put(3, 600);
        LEVEL_XP.put(4, 850);
        LEVEL_XP.put(5, 1100);
        LEVEL_XP.put(6, 1400);
        LEVEL_XP.put(7, 1750);
        LEVEL_XP.put(8, 2150);
        LEVEL_XP.put(9, 2600);
        LEVEL_XP.put(10, 3100);
    }
}
