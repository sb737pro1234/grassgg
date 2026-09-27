package me.sbpro.grassggalliances;

import java.util.List;

/**
 * Central place for all GrassGGAlliances messages and GUI text.
 *
 * Normal text is §f (white).
 */
public final class Messages {
    private Messages() {
    }

    public static final String ALLIANCE_COLOR = "§x§4§3§A§0§4§7";
    public static final String PREFIX = ALLIANCE_COLOR + "§lALLIANCES §8» §f";

    public static final String NOT_FOUND = "§fNo player or alliance matched '" + ALLIANCE_COLOR + "%input%§f'.";
    public static final List<String> INFO_FORMAT = List.of(
            "§8&m==============================",
            "§fAlliance: " + ALLIANCE_COLOR + "%alliance%",
            "§fOwner: " + ALLIANCE_COLOR + "%owner%",
            "§fMembers: " + ALLIANCE_COLOR + "%members%",
            "§8&m=============================="
    );

    public static final String PLAYER_ONLY = "§cOnly players can use this command.";
    public static final String USAGE = "§fUsage: " + ALLIANCE_COLOR + "/a <create|info|delete|chat|invite|accept|kick|leave|top|level>";
    public static final String CREATE_USAGE = "§fUsage: " + ALLIANCE_COLOR + "/a create <name>";
    public static final String INVITE_USAGE = "§fUsage: " + ALLIANCE_COLOR + "/a invite <player>";
    public static final String ACCEPT_USAGE = "§fUsage: " + ALLIANCE_COLOR + "/a accept";
    public static final String KICK_USAGE = "§fUsage: " + ALLIANCE_COLOR + "/a kick <player>";
    public static final String OWNER_ONLY_LEAVE = "§fYou are the alliance owner. Use " + ALLIANCE_COLOR + "/a delete §fif you want to remove your alliance.";
    public static final String LEAVE_FAILED = "§cYou could not leave the alliance. Please try again.";
    public static final String LEFT = "§fYou have left the alliance " + ALLIANCE_COLOR + "%alliance%§f.";
    public static final String MEMBER_LEFT = ALLIANCE_COLOR + "%player% §fhas left the alliance.";
    public static final String INVALID_NAME = "§cAlliance names must be plain text only. No hex colors, no & color codes.";
    public static final String NAME_LENGTH = "§cAlliance names must be between 3 and 24 characters.";
    public static final String ALREADY_IN_ALLIANCE = "§cYou are already in an alliance.";
    public static final String ALLIANCE_EXISTS = "§cThat alliance already exists.";
    public static final String CREATED = "§fAlliance " + ALLIANCE_COLOR + "'%alliance%' §fcreated.";
    public static final String ALLIANCE_FULL = "§cThat alliance already has the maximum of 5 members.";
    public static final String OWNER_ONLY_INVITE = "§cOnly the alliance owner can invite players.";
    public static final String OWNER_ONLY_DELETE = "§cOnly the alliance owner can delete the alliance.";
    public static final String OWNER_ONLY_KICK = "§cOnly the alliance owner can kick members.";
    public static final String NO_ALLIANCE = "§cYou are not in an alliance.";
    public static final String NO_PENDING_INVITE = "§cYou do not have a pending alliance invite.";
    public static final String TARGET_NOT_ONLINE = "§cThat player must be online.";
    public static final String TARGET_ALREADY_IN_ALLIANCE = "§cThat player is already in an alliance.";
    public static final String CANNOT_KICK_OWNER = "§cYou cannot kick the alliance owner.";
    public static final String CANNOT_KICK_SELF = "§fUse " + ALLIANCE_COLOR + "/a delete §fif you want to remove your alliance.";
    public static final String PLAYER_NOT_IN_YOUR_ALLIANCE = "§cThat player is not in your alliance.";
    public static final String INVITE_SENT = "§fInvite sent to " + ALLIANCE_COLOR + "%player%§f.";
    public static final String INVITE_RECEIVED = "§fYou have been invited to join " + ALLIANCE_COLOR + "'%alliance%' §fby " + ALLIANCE_COLOR + "%owner%§f. Type " + ALLIANCE_COLOR + "/a accept §fto join.";
    public static final String INVITE_EXPIRED = "§cYour pending alliance invite is no longer valid.";
    public static final String JOINED = "§fYou joined " + ALLIANCE_COLOR + "'%alliance%'§f.";
    public static final String MEMBER_JOINED = ALLIANCE_COLOR + "%player% §fhas joined the alliance.";
    public static final String KICKED_TARGET = "§fYou were kicked from " + ALLIANCE_COLOR + "'%alliance%'§f.";
    public static final String KICKED_BY_OWNER = "§fYou kicked " + ALLIANCE_COLOR + "%player% §ffrom the alliance.";
    public static final String KICKED_BROADCAST = ALLIANCE_COLOR + "%player% §fwas kicked from the alliance.";
    public static final String DELETE_TITLE = "§fDelete Alliance?";
    public static final String DELETE_OPENED = "§fPlease confirm deletion in the GUI.";
    public static final String DELETE_CANCELLED = "§fAlliance deletion cancelled.";
    public static final String DELETED = "§fAlliance " + ALLIANCE_COLOR + "'%alliance%' §fdeleted.";
    public static final String CHAT_ENABLED = "§fAlliance chat enabled.";
    public static final String CHAT_DISABLED = "§fAlliance chat disabled. You are back in global chat.";
    public static final String CHAT_FORMAT = "§8[" + ALLIANCE_COLOR + "%alliance%§8] " + ALLIANCE_COLOR + "%player%§7: §f%message%";
    public static final List<String> TOP_HEADER = List.of(
            ALLIANCE_COLOR + "Alliance Top",
            "§fTop alliances by alliance XP"
    );
    public static final String TOP_ENTRY = ALLIANCE_COLOR + "#%position% §f%alliance% §8- §fLevel " + ALLIANCE_COLOR + "%level% §8- §fXP: " + ALLIANCE_COLOR + "%xp%";
    public static final String TOP_EMPTY = "§fNo alliances have been created yet.";

    // GUI titles and button names use the alliance colour, no italics/bold, and standard case.
    public static final String INFO_GUI_TITLE = ALLIANCE_COLOR + "Alliance Information";
    public static final String MEMBERS_GUI_TITLE = ALLIANCE_COLOR + "Alliance Members";
    public static final String INFO_GUI_MEMBERS = ALLIANCE_COLOR + "Members";
    public static final String INFO_GUI_LEVEL = ALLIANCE_COLOR + "Alliance Level";
    public static final String INFO_GUI_XP_VALUES = ALLIANCE_COLOR + "Alliance XP";
    public static final String LEVEL_TITLE = ALLIANCE_COLOR + "Alliance Level";
    public static final String XP_VALUES_TITLE = ALLIANCE_COLOR + "Alliance XP";
    public static final String LEVEL_GUI_TITLE = ALLIANCE_COLOR + "Alliance Information";
    public static final String XP_VALUES_BUTTON = ALLIANCE_COLOR + "Alliance XP";
    public static final String GO_BACK = ALLIANCE_COLOR + "Go Back";
    public static final String NEXT_PAGE = ALLIANCE_COLOR + "Next Page";
    public static final String PREVIOUS_PAGE = ALLIANCE_COLOR + "Previous Page";
    public static final String NO_NEXT_PAGE = "§fThere is no next page.";
    public static final String NO_PREVIOUS_PAGE = "§fThere is no previous page.";
    public static final String LEVEL_UP = "§fYour alliance reached level " + ALLIANCE_COLOR + "%level%§f!";

    public static String get(String path) {
        return switch (path) {
            case "not-found" -> NOT_FOUND;
            case "prefix" -> PREFIX;
            case "player-only" -> PLAYER_ONLY;
            case "usage" -> USAGE;
            case "create-usage" -> CREATE_USAGE;
            case "invite-usage" -> INVITE_USAGE;
            case "accept-usage" -> ACCEPT_USAGE;
            case "kick-usage" -> KICK_USAGE;
            case "owner-only-leave" -> OWNER_ONLY_LEAVE;
            case "leave-failed" -> LEAVE_FAILED;
            case "left" -> LEFT;
            case "member-left" -> MEMBER_LEFT;
            case "invalid-name" -> INVALID_NAME;
            case "name-length" -> NAME_LENGTH;
            case "already-in-alliance" -> ALREADY_IN_ALLIANCE;
            case "alliance-exists" -> ALLIANCE_EXISTS;
            case "created" -> CREATED;
            case "alliance-full" -> ALLIANCE_FULL;
            case "owner-only-invite" -> OWNER_ONLY_INVITE;
            case "owner-only-delete" -> OWNER_ONLY_DELETE;
            case "owner-only-kick" -> OWNER_ONLY_KICK;
            case "no-alliance" -> NO_ALLIANCE;
            case "no-pending-invite" -> NO_PENDING_INVITE;
            case "target-not-online" -> TARGET_NOT_ONLINE;
            case "target-already-in-alliance" -> TARGET_ALREADY_IN_ALLIANCE;
            case "cannot-kick-owner" -> CANNOT_KICK_OWNER;
            case "cannot-kick-self" -> CANNOT_KICK_SELF;
            case "player-not-in-your-alliance" -> PLAYER_NOT_IN_YOUR_ALLIANCE;
            case "invite-sent" -> INVITE_SENT;
            case "invite-received" -> INVITE_RECEIVED;
            case "invite-expired" -> INVITE_EXPIRED;
            case "joined" -> JOINED;
            case "member-joined" -> MEMBER_JOINED;
            case "kicked-target" -> KICKED_TARGET;
            case "kicked-by-owner" -> KICKED_BY_OWNER;
            case "kicked-broadcast" -> KICKED_BROADCAST;
            case "delete-title" -> DELETE_TITLE;
            case "delete-opened" -> DELETE_OPENED;
            case "delete-cancelled" -> DELETE_CANCELLED;
            case "deleted" -> DELETED;
            case "chat-enabled" -> CHAT_ENABLED;
            case "chat-disabled" -> CHAT_DISABLED;
            case "chat-format" -> CHAT_FORMAT;
            case "top-entry" -> TOP_ENTRY;
            case "top-empty" -> TOP_EMPTY;
            case "info-gui-title" -> INFO_GUI_TITLE;
            case "members-gui-title" -> MEMBERS_GUI_TITLE;
            case "info-gui-members" -> INFO_GUI_MEMBERS;
            case "info-gui-level" -> INFO_GUI_LEVEL;
            case "info-gui-xp-values" -> INFO_GUI_XP_VALUES;
            case "level-title" -> LEVEL_TITLE;
            case "xp-values-title" -> XP_VALUES_TITLE;
            case "level-gui-title" -> LEVEL_GUI_TITLE;
            case "xp-values-button" -> XP_VALUES_BUTTON;
            case "go-back" -> GO_BACK;
            case "next-page" -> NEXT_PAGE;
            case "previous-page" -> PREVIOUS_PAGE;
            case "no-next-page" -> NO_NEXT_PAGE;
            case "no-previous-page" -> NO_PREVIOUS_PAGE;
            case "level-up" -> LEVEL_UP;
            default -> "";
        };
    }
}
