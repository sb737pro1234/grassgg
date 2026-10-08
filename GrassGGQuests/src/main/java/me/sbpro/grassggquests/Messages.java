package me.sbpro.grassggquests;

import java.util.List;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;

public final class Messages {
    private Messages() {}

    public static String CUSTOM_COLOR = "§x§1§4§b§8§a§6";
    public static String WHITE = "§f";
    public static String RED = "§c";
    public static String PREFIX = CUSTOM_COLOR + "§lQUESTS §8» " + WHITE;

    public static String NO_PERMISSION = RED + "You do not have permission to use this command.";
    public static String PLAYER_ONLY = RED + "This command can only be used by a player.";
    public static String INVALID_PLAYER = RED + "That player could not be found.";
    public static String INVALID_QUEST = RED + "That quest could not be found.";
    public static String INVALID_NUMBER = RED + "Please enter a valid number.";
    public static String USAGE = RED + "Invalid command. Use /quests for help.";
    public static String MAX_QUESTS = RED + "You already have the maximum number of active quests.";
    public static String NOT_ENOUGH_COINS = RED + "You do not have enough coins.";
    public static String ALREADY_ACTIVE = RED + "That quest is already active for this player.";

    public static String EASY_SUCCESS = WHITE + "Quest changed to Easy for " + CUSTOM_COLOR + "2 coins" + WHITE + ".";
    public static String REROLL_SUCCESS = WHITE + "Quest Easified for " + CUSTOM_COLOR + "2 coins" + WHITE + ".";
    public static String QUEST_COMPLETED = WHITE + "Quest completed: " + CUSTOM_COLOR + "%quest%";
    public static String QUEST_GIVEN_TO_YOU = WHITE + "You received quest: " + CUSTOM_COLOR + "%quest%" + WHITE + ".";
    public static String RELOAD_SUCCESS = WHITE + "Configuration reloaded successfully.";
    public static String RESET_SUCCESS = WHITE + "Quest data for " + CUSTOM_COLOR + "%player%" + WHITE + " has been reset.";
    public static String GIVE_SUCCESS = WHITE + "Quest " + CUSTOM_COLOR + "%quest%" + WHITE + " has been given to " + CUSTOM_COLOR + "%player%" + WHITE + ".";

    public static String QUEST_COMPLETION_TITLE = CUSTOM_COLOR + "Quest Complete!";
    public static String QUEST_COMPLETION_SUBTITLE = WHITE + "+%reward% coins";
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
    public static List<String> ACTIVE_QUESTS_LORE = List.of(WHITE + "View your active quests.");
    public static String COIN_SHOP_NAME = "§x§F§F§D§5§4§ACoin Shop";
    public static List<String> COIN_SHOP_LORE = List.of(WHITE + "Open the Coin Shop.");
    public static String QUEST_TITLE_FORMAT = CUSTOM_COLOR + "%quest%";
    public static String MEDIUM_LABEL = "§fDiffculty:" + WHITE +"Normal";
    public static String EASY_LABEL = "§fDiffculty: §aEasy";
    public static String PROGRESS_LINE = WHITE + "Progress: " + CUSTOM_COLOR + "%progress%" + WHITE + " / " + CUSTOM_COLOR + "%required%";
    public static String REWARD_LINE = WHITE + "Reward: " + CUSTOM_COLOR + "%reward% coins";
    public static String REROLL_BUTTON_NAME = "§eEasify Quest";
    public static List<String> REROLL_BUTTON_LORE = List.of(WHITE + "Turn this Medium quest into an Easy quest for " + CUSTOM_COLOR + "2 coins" + WHITE + ".");

    public static String EASIFY_CONFIRM_TITLE = CUSTOM_COLOR + "Confirm Easify";
    public static String CONFIRM_NAME = "§aConfirm";
    public static List<String> CONFIRM_LORE = List.of(WHITE + "Turn this Medium quest into an Easy quest.");
    public static String CANCEL_NAME = "§cCancel";
    public static List<String> CANCEL_LORE = List.of(WHITE + "Return to your quests.");

    public static String QUEST_MENU_TITLE = CUSTOM_COLOR + "Quests";
    public static String QUEST_LIST_TITLE = CUSTOM_COLOR + "Active Quests";
}
