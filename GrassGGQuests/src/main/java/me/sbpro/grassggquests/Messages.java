package me.sbpro.grassggquests;

import java.util.List;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public final class Messages {
    private Messages() {}

    public static String CUSTOM_COLOR = "§x§1§4§b§8§a§6";
    public static String MAIN_COLOR = "§f";
    public static String WHITE = MAIN_COLOR;
    public static String ERROR_COLOR = "§c";
    public static String PREFIX = CUSTOM_COLOR + "§lQUESTS §8» " + MAIN_COLOR;

    public static String NO_PERMISSION = ERROR_COLOR + "You do not have permission to use this command.";
    public static String PLAYER_ONLY = ERROR_COLOR + "This command can only be used by a player.";
    public static String INVALID_PLAYER = ERROR_COLOR + "That player could not be found.";
    public static String INVALID_QUEST = ERROR_COLOR + "That quest could not be found.";
    public static String INVALID_NUMBER = ERROR_COLOR + "Please enter a valid number.";
    public static String USAGE = ERROR_COLOR + "Invalid command. Use /quests for help.";
    public static String MAX_QUESTS = ERROR_COLOR + "You already have the maximum number of active quests.";
    public static String NOT_ENOUGH_COINS = ERROR_COLOR + "You do not have enough coins.";
    public static String EASY_SUCCESS = CUSTOM_COLOR + "Quest changed to Easy for " + MAIN_COLOR + "2 coins" + CUSTOM_COLOR + ".";
    public static String REROLL_SUCCESS = CUSTOM_COLOR + "Quest Easified for " + MAIN_COLOR + "2 coins" + CUSTOM_COLOR + ".";
    public static String QUEST_COMPLETED = CUSTOM_COLOR + "Quest completed: " + MAIN_COLOR + "%quest%";
    public static String QUEST_GIVEN_TO_YOU = CUSTOM_COLOR + "You received quest: " + MAIN_COLOR + "%quest%" + CUSTOM_COLOR + ".";
    public static String ALREADY_ACTIVE = ERROR_COLOR + "That quest is already active for this player.";
    public static String RELOAD_SUCCESS = CUSTOM_COLOR + "Configuration reloaded successfully.";
    public static String RESET_SUCCESS = CUSTOM_COLOR + "Quest data for " + MAIN_COLOR + "%player%" + CUSTOM_COLOR + " has been reset.";
    public static String GIVE_SUCCESS = CUSTOM_COLOR + "Quest " + MAIN_COLOR + "%quest%" + CUSTOM_COLOR + " has been given to " + MAIN_COLOR + "%player%" + CUSTOM_COLOR + ".";

    public static String QUEST_COMPLETION_TITLE = CUSTOM_COLOR + "Quest Complete!";
    public static String QUEST_COMPLETION_SUBTITLE = MAIN_COLOR + "+%reward% coins";
    public static int QUEST_COMPLETION_FADE_IN = 10;
    public static int QUEST_COMPLETION_STAY = 50;
    public static int QUEST_COMPLETION_FADE_OUT = 10;

    public static String QUEST_COMPLETION_BOX_TOP = "§8-----------------------------------------------";
    public static String QUEST_COMPLETION_BOX_BOTTOM = "§8-----------------------------------------------";
    public static String QUEST_COMPLETION_BOX_MESSAGE = PREFIX + WHITE + "You completed " + CUSTOM_COLOR + "%quest%" + WHITE + " and received " + CUSTOM_COLOR + "+%reward% coins" + WHITE + ".";

    public static BarColor BOSSBAR_COLOR = BarColor.BLUE;
    public static BarStyle BOSSBAR_STYLE = BarStyle.SOLID;
    public static String BOSSBAR_TITLE = CUSTOM_COLOR + "%quest%" + WHITE + " (%progress%/%required%)";
    public static long BOSSBAR_DISPLAY_TICKS = 100L;

    public static String ACTIVE_QUESTS_NAME = CUSTOM_COLOR + "Active Quests";
    public static List<String> ACTIVE_QUESTS_LORE = List.of(MAIN_COLOR + "View your active quests.");
    public static String COIN_SHOP_NAME = "§x§F§F§D§5§4§ACoin Shop";
    public static List<String> COIN_SHOP_LORE = List.of(MAIN_COLOR + "Open the Coin Shop.");
    public static String QUEST_TITLE_FORMAT = CUSTOM_COLOR + "%quest%";
    public static String QUEST_INFO_PREFIX = MAIN_COLOR;
    public static String MEDIUM_LABEL = "§eMedium";
    public static String EASY_LABEL = "§aEasy";
    public static String PROGRESS_LINE = MAIN_COLOR + "Progress: " + CUSTOM_COLOR + "%progress%" + MAIN_COLOR + " / " + CUSTOM_COLOR + "%required%";
    public static String REWARD_LINE = MAIN_COLOR + "Reward: " + CUSTOM_COLOR + "%reward% coins";
    public static String REROLL_BUTTON_NAME = "§eEasify Quest";
    public static List<String> REROLL_BUTTON_LORE = List.of(MAIN_COLOR + "Turn this Medium quest into an Easy quest for " + CUSTOM_COLOR + "2 coins" + MAIN_COLOR + ".");

    public static String EASIFY_CONFIRM_TITLE = CUSTOM_COLOR + "Confirm Easify";
    public static String CONFIRM_NAME = "§aConfirm";
    public static List<String> CONFIRM_LORE = List.of(MAIN_COLOR + "Turn this Medium quest into an Easy quest.");
    public static String CANCEL_NAME = "§cCancel";
    public static List<String> CANCEL_LORE = List.of(MAIN_COLOR + "Return to your quests.");

    public static String QUEST_MENU_TITLE = CUSTOM_COLOR + "Quests";
    public static String QUEST_LIST_TITLE = CUSTOM_COLOR + "Active Quests";
}
