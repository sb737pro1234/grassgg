package me.sbpro.grassggspawns;

import org.bukkit.ChatColor;

public final class Messages {

    private Messages() {
    }

    /*
     * ==========================================
     * PREFIX
     * ==========================================
     */

    public static final String PREFIX =
            "§x§F§F§D§6§0§0§lWORLD §8» §f";


    /*
     * ==========================================
     * COMMAND MESSAGES
     * ==========================================
     */

    public static final String NO_PERMISSION =
            PREFIX + "You do not have permission to do that.";

    public static final String PLAYER_ONLY =
            PREFIX + "Only players can use this command.";

    public static final String RELOADED =
            PREFIX + "Configuration reloaded successfully.";

    public static final String INVALID_ARGUMENT =
            PREFIX + "Usage: §e/grassggspawns reload";

    /*
     * ==========================================
     * PROTECTION MESSAGES
     * ==========================================
     */

    public static final String BLOCK_BREAK =
            PREFIX + "You cannot break blocks here.";

    public static final String BLOCK_PLACE =
            PREFIX + "You cannot place blocks here.";

    public static final String BLOCK_INTERACT =
            PREFIX + "You cannot interact with blocks here.";

    public static final String ENTITY_INTERACT =
            PREFIX + "You cannot interact with entities here.";

    public static final String PVP =
            PREFIX + "You cannot PvP here.";

    public static final String ITEM_DROP =
            PREFIX + "You cannot drop items here.";

    public static final String ITEM_PICKUP =
            PREFIX + "You cannot pick up items here.";

    /*
     * ==========================================
     * UTILITY
     * ==========================================
     */

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}