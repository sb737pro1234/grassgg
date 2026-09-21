package me.sbpro.grassggcombat;

public final class Messages {

    private Messages() {
        // Utility class
    }

    /*
     * Prefix
     */
    public static final String PREFIX =
            "§x§E§9§1§E§6§3§lCOMBAT §8» §f";

    public static final String COLOR =
            "§x§E§9§1§E§6§3";

    /*
     * Messages
     */
    public static final String COMMAND_BLOCKED =
            PREFIX + "§cYou cannot use commands while in combat!";

    public static final String COMBAT_LOGOUT =
            "§8[§4☠§8] §4[PLAYER] §flogged out during combat.";

    public static final String ENTERED_COMBAT =
            PREFIX + "§fYou are now in combat with " + COLOR +"[OPPONENT]" + "§f! You cannot log out or use commands for " + COLOR + "[TIME]" + "§fs.";

    public static final String LEFT_COMBAT =
            PREFIX + "§fYou are no longer in combat.";
    /*
     * Action Bar
     */
    public static final String COMBAT_ACTION_BAR =
            "§fCombat: " + COLOR + "[TIME]" + "§fs";
}
