package me.sbpro.grassggsurvival.commands.baltop;

import java.text.DecimalFormat;

/**
 * All editable messages and display settings for the /baltop menu.
 *
 * Colours use the Minecraft section symbol (§).
 */
public final class BalTopMessages {

    private BalTopMessages() {
    }

    // Menu
    public static String MENU_TITLE = "§2Balance Top";

    // Top player head
    public static String TOP_PLAYER_NAME = "§f%player% (§2#%position%§f)";
    public static String TOP_PLAYER_BALANCE = "§fBalance: §2$%balance%";
    public static String TOP_PLAYER_BLANK_LORE = "";

    // Your own balance head
    public static String YOUR_BALANCE_NAME = "§2Your Balance";
    public static String YOUR_BALANCE_BLANK_LORE = "";
    public static String YOUR_BALANCE_BALANCE = "§fBalance: §2$%balance%";
    public static String YOUR_BALANCE_POSITION = "§fPosition: §2#%position%";
    public static String YOUR_BALANCE_UNRANKED = "§fPosition: §2Unranked";

    // Messages
    public static String ECONOMY_UNAVAILABLE = "§cThe economy system is currently unavailable.";
    public static String PLAYER_ONLY = "§cOnly players can use this command.";
    public static String RELOADED = "§2Balance Top messages have been reloaded.";
    public static String NO_PERMISSION = "§cYou do not have permission to do that.";

    // Money formatting
    public static String MONEY_FORMAT = "#,##0";

    /**
     * Rebuilds the formatter used by the menu from MONEY_FORMAT.
     * This exists so /baltop reload can apply formatting changes immediately.
     */
    public static DecimalFormat createMoneyFormatter() {
        return new DecimalFormat(MONEY_FORMAT);
    }

    public static String topPlayerName(int position, String playerName) {
        return TOP_PLAYER_NAME
                .replace("%position%", String.valueOf(position))
                .replace("%player%", playerName);
    }

    public static String topPlayerBalance(double balance) {
        return TOP_PLAYER_BALANCE
                .replace("%balance%", createMoneyFormatter().format(balance));
    }

    public static String yourBalanceBalance(double balance) {
        return YOUR_BALANCE_BALANCE
                .replace("%balance%", createMoneyFormatter().format(balance));
    }

    public static String yourBalancePosition(int position) {
        if (position > 0) {
            return YOUR_BALANCE_POSITION
                    .replace("%position%", String.valueOf(position));
        }

        return YOUR_BALANCE_UNRANKED;
    }
}
