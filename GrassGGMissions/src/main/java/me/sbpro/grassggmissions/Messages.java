package me.sbpro.grassggmissions;

public final class Messages {

    private Messages() {
    }

    public static final String PREFIX = "§x§F§F§6§D§0§0§lMISSIONS §8» §f";

    /**
     * Command run by console when a player completes a mission.
     * %player% is replaced with the completing player's name.
     */
    public static final String MISSION_REWARD_COMMAND = "coins give %player% 10";

    public static final String MISSION_COMPLETE_TITLE = "§x§F§F§6§D§0§0§lMission Complete!";
    public static final String MISSION_COMPLETE_SUBTITLE = "§x§F§F§D§5§4§A+10 Coins";
    public static final String MISSION_COMPLETE_MESSAGE = "§aMission completed!";
    public static final String MISSION_REWARD_MESSAGE = "§aYou earned §x§F§F§D§5§4§A10 Coins§a.";
}
