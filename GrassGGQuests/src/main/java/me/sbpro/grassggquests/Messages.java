package me.sbpro.grassggquests;

import java.util.List;

public final class Messages {
    private Messages() {}

    // Colours - use § codes, including §x hex colours.
    public static String CUSTOM_COLOR = "§x§A§0§2§0§F§F";
    public static String MAIN_COLOR = "§f";
    public static String ERROR_COLOR = "§c";

    public static String PREFIX = CUSTOM_COLOR + "§lQUESTS §8» " + MAIN_COLOR;

    public static String NO_PERMISSION = ERROR_COLOR + "You do not have permission to use this command.";
    public static String PLAYER_ONLY = ERROR_COLOR + "This command can only be used by a player.";
    public static String INVALID_PLAYER = ERROR_COLOR + "That player could not be found.";
    public static String INVALID_QUEST = ERROR_COLOR + "That quest could not be found.";
    public static String INVALID_SHOP_ITEM = ERROR_COLOR + "That shop item could not be found.";
    public static String INVALID_NUMBER = ERROR_COLOR + "Please enter a valid number.";
    public static String USAGE = ERROR_COLOR + "Invalid command. Use /quests for help.";
    public static String MAX_QUESTS = ERROR_COLOR + "You already have the maximum number of active quests.";
    public static String NOT_ENOUGH_POINTS = ERROR_COLOR + "You do not have enough Quest Points.";
    public static String PURCHASE_SUCCESS = CUSTOM_COLOR + "Purchase successful!";
    public static String QUEST_COMPLETED = CUSTOM_COLOR + "Quest completed: " + MAIN_COLOR + "%quest% " + CUSTOM_COLOR + "(+%xp% XP)";
    public static String LEVEL_UP = CUSTOM_COLOR + "Quest Level increased to " + MAIN_COLOR + "%level%" + CUSTOM_COLOR + "!";
    public static String POINT_RECEIVED = CUSTOM_COLOR + "You received " + MAIN_COLOR + "1 Quest Point" + CUSTOM_COLOR + ".";
    public static String RELOAD_SUCCESS = CUSTOM_COLOR + "Configuration reloaded successfully.";
    public static String DISPLAY_ITEM_CHANGED = CUSTOM_COLOR + "Display item for " + MAIN_COLOR + "%item%" + CUSTOM_COLOR + " has been changed.";
    public static String SET_LEVEL_SUCCESS = CUSTOM_COLOR + "%player%'s Quest Level is now " + MAIN_COLOR + "%level%" + CUSTOM_COLOR + ".";
    public static String RESET_SUCCESS = CUSTOM_COLOR + "Quest data for " + MAIN_COLOR + "%player%" + CUSTOM_COLOR + " has been reset.";
    public static String GIVE_SUCCESS = CUSTOM_COLOR + "Quest " + MAIN_COLOR + "%quest%" + CUSTOM_COLOR + " has been given to " + MAIN_COLOR + "%player%" + CUSTOM_COLOR + ".";
    public static String ALREADY_ACTIVE = ERROR_COLOR + "That quest is already active for this player.";
    public static String QUEST_GIVEN_TO_YOU = CUSTOM_COLOR + "You received quest: " + MAIN_COLOR + "%quest%" + CUSTOM_COLOR + ".";

    public static String DAILY_MISSIONS_NAME = "§x§F§F§6§D§0§0Daily Missions";
    public static List<String> DAILY_MISSIONS_LORE = List.of(MAIN_COLOR + "Open the Daily Missions menu.");
    public static String ACTIVE_QUESTS_NAME = CUSTOM_COLOR + "Active Quests";
    public static List<String> ACTIVE_QUESTS_LORE = List.of(MAIN_COLOR + "View your active quests.");
    public static String QUEST_LEVEL_NAME = CUSTOM_COLOR + "Quest Level";
    public static List<String> QUEST_LEVEL_LORE = List.of(MAIN_COLOR + "View your Quest Level and XP.");
    public static String QUEST_SHOP_NAME = CUSTOM_COLOR + "Quest Shop";
    public static List<String> QUEST_SHOP_LORE = List.of(MAIN_COLOR + "Spend your Quest Points.");

    public static String QUEST_LEVEL_DISPLAY_NAME = CUSTOM_COLOR + "Quest Level";
    public static String QUEST_LEVEL_LINE = MAIN_COLOR + "Quest Level: " + CUSTOM_COLOR + "%level%";
    public static String QUEST_XP_LINE = MAIN_COLOR + "XP: " + CUSTOM_COLOR + "%xp%" + MAIN_COLOR + " / " + CUSTOM_COLOR + "%required%";
    public static String PROGRESS_FILLED = "§a█";
    public static String PROGRESS_EMPTY = "§7░";
    public static int PROGRESS_LENGTH = 20;

    public static String QUEST_POINTS_DISPLAY_NAME = CUSTOM_COLOR + "Quest Points";
    public static List<String> QUEST_POINTS_LORE = List.of(MAIN_COLOR + "You have: " + CUSTOM_COLOR + "%points%");

    public static String CONFIRM_NAME = "§aConfirm Purchase";
    public static List<String> CONFIRM_LORE = List.of(MAIN_COLOR + "Click to purchase this item.");
    public static String CANCEL_NAME = "§cCancel";
    public static List<String> CANCEL_LORE = List.of(MAIN_COLOR + "Return to the Quest Shop.");

    public static String QUEST_TITLE_FORMAT = CUSTOM_COLOR + "%quest%";
    public static String QUEST_INFO_PREFIX = MAIN_COLOR;

    public static String SHOP_TITLE = CUSTOM_COLOR + "Quest Shop";
    public static String CONFIRM_TITLE = CUSTOM_COLOR + "Confirm Purchase";
    public static String QUEST_MENU_TITLE = CUSTOM_COLOR + "Quests";
    public static String QUEST_LIST_TITLE = CUSTOM_COLOR + "Active Quests";
    public static String QUEST_LEVEL_TITLE = CUSTOM_COLOR + "Quest Level";
}
