package me.sbpro.grassggsupertools;

import org.bukkit.ChatColor;

public final class Messages {

    private Messages() {
    }

    /*
     * Main colour is used for normal text.
     * Custom colour is used for highlighted values such as [PLAYER] and [TOOL].
     *
     * Change CUSTOM_COLOR to any Bukkit ChatColor you want.
     */
    public static final ChatColor MAIN_COLOR = ChatColor.WHITE;
    public static final ChatColor CUSTOM_COLOR = ChatColor.AQUA;

    public static final String PREFIX = "§x§5§6§E§0§F§D§lSUPERTOOLS §8» §f";

    public static String playerGiven(String player, String tool) {
        return PREFIX + MAIN_COLOR + "You gave " + CUSTOM_COLOR + player
                + MAIN_COLOR + " " + CUSTOM_COLOR + tool + MAIN_COLOR + ".";
    }

    public static String playerOnly() {
        return PREFIX + MAIN_COLOR + "Only players can use this command.";
    }

    public static String noPermission() {
        return PREFIX + ChatColor.RED + "You do not have permission to use this command.";
    }

    public static String usage() {
        return PREFIX + MAIN_COLOR + "Usage: " + CUSTOM_COLOR + "/supertools give [player] [tool]";
    }

    public static String unknownTool() {
        return PREFIX + ChatColor.RED + "Unknown tool.";
    }

    public static String playerNotFound() {
        return PREFIX + ChatColor.RED + "That player could not be found.";
    }
}
