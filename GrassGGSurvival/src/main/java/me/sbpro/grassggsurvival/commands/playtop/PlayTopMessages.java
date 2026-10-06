package me.sbpro.grassggsurvival.commands.playtop;

/**
 * All editable messages and display settings for the /playtop menu.
 *
 * Colours use the Minecraft section symbol (§).
 */
public final class PlayTopMessages {

    private PlayTopMessages() {
    }

    // Menu
    public static String MENU_TITLE = "§2Playtime Top";

    // Top player head
    public static String TOP_PLAYER_NAME = "§f%player% (§2#%position%§f)";
    public static String TOP_PLAYER_PLAYTIME = "§fPlaytime: §2%playtime%";
    public static String TOP_PLAYER_BLANK_LORE = "";

    // Your own playtime head
    public static String YOUR_PLAYTIME_NAME = "§2Your Playtime";
    public static String YOUR_PLAYTIME_BLANK_LORE = "";
    public static String YOUR_PLAYTIME_PLAYTIME = "§fPlaytime: §2%playtime%";
    public static String YOUR_PLAYTIME_POSITION = "§fPosition: §2#%position%";
    public static String YOUR_PLAYTIME_UNRANKED = "§fPosition: §2Unranked";

    // Messages
    public static String PLAYER_ONLY = "§cOnly players can use this command.";
    public static String RELOADED = "§2Playtime Top messages have been reloaded.";
    public static String NO_PERMISSION = "§cYou do not have permission to do that.";

    // Playtime formatting (units are joined with a space, zero-value units are skipped,
    // and the minutes unit is always shown if nothing else is, e.g. "3d 4h 12m" or "0m")
    public static String DAYS_UNIT = "d";
    public static String HOURS_UNIT = "h";
    public static String MINUTES_UNIT = "m";

    /**
     * Converts a playtime in ticks (20 ticks = 1 second) into a readable string.
     */
    public static String formatPlaytime(long ticks) {
        long totalMinutes = Math.max(0, ticks) / 20L / 60L;

        long days = totalMinutes / (60L * 24L);
        long hours = (totalMinutes / 60L) % 24L;
        long minutes = totalMinutes % 60L;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(days).append(DAYS_UNIT).append(' ');
        }

        if (days > 0 || hours > 0) {
            builder.append(hours).append(HOURS_UNIT).append(' ');
        }

        builder.append(minutes).append(MINUTES_UNIT);

        return builder.toString();
    }

    public static String topPlayerName(int position, String playerName) {
        return TOP_PLAYER_NAME
                .replace("%position%", String.valueOf(position))
                .replace("%player%", playerName);
    }

    public static String topPlayerPlaytime(long ticks) {
        return TOP_PLAYER_PLAYTIME
                .replace("%playtime%", formatPlaytime(ticks));
    }

    public static String yourPlaytimePlaytime(long ticks) {
        return YOUR_PLAYTIME_PLAYTIME
                .replace("%playtime%", formatPlaytime(ticks));
    }

    public static String yourPlaytimePosition(int position) {
        if (position > 0) {
            return YOUR_PLAYTIME_POSITION
                    .replace("%position%", String.valueOf(position));
        }

        return YOUR_PLAYTIME_UNRANKED;
    }
}
