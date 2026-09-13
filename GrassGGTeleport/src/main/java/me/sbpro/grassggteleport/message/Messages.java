package me.sbpro.grassggteleport.message;

import org.bukkit.entity.Player;

public final class Messages {

    private static final String PREFIX = "§x§0§0§A§8§F§F§lTELEPORT §8» ";
    private static final String WHITE = "§f";
    private static final String RED = "§c";
    private static final String HIGHLIGHT = "§x§0§0§A§8§F§F";

    private Messages() {
    }

    public static void send(Player player, String message) {
        player.sendMessage(PREFIX + WHITE + message);
    }

    public static void sendError(Player player, String message) {
        player.sendMessage(PREFIX + RED + message);
    }

    public static String prefix() {
        return PREFIX;
    }

    public static String white() {
        return WHITE;
    }

    public static String red() {
        return RED;
    }

    public static String highlight() {
        return HIGHLIGHT;
    }

    public static String teleportingIn(int seconds) {
        String secondText = seconds == 1 ? "second" : "seconds";

        return PREFIX
                + WHITE + "Teleporting in "
                + HIGHLIGHT + seconds
                + WHITE + " " + secondText + ".";
    }

    public static String teleportStarted() {
        return PREFIX + WHITE + "Teleport started.";
    }

    public static String teleportSuccessful() {
        return PREFIX + WHITE + "Teleport successful.";
    }

    public static String teleportCancelled() {
        return PREFIX + RED + "Teleport cancelled.";
    }

    public static String teleportCancelledMovement() {
        return PREFIX + RED  + "Teleport cancelled due to movement.";
    }

    public static String requestSent(Player target) {
        return PREFIX
                + WHITE + "Teleport request sent to "
                + HIGHLIGHT + target.getName()
                + WHITE + ".";
    }

    public static String requestAlreadySent(Player target) {
        return PREFIX
                + RED + "You have already sent a teleport request to "
                + target.getName()
                + ".";
    }

    public static String requestExpiredTo(Player target) {
        return PREFIX
                + RED + "Your teleport request to "
                + RED + target.getName()
                + RED + " has expired.";
    }

    public static String requestExpiredFrom(Player requester) {
        return PREFIX
                + RED + "The teleport request from "
                + RED + requester.getName()
                + RED + " has expired.";
    }

    public static String requestReceived(Player sender) {
        return PREFIX
                + WHITE + HIGHLIGHT + sender.getName()
                + WHITE + " has requested to teleport to you.";
    }

    public static String requestAccepted(Player sender) {
        return PREFIX
                + WHITE + "Teleport request from "
                + HIGHLIGHT + sender.getName()
                + WHITE + " accepted.";
    }

    public static String requestDenied(Player sender) {
        return PREFIX
                + RED + "Teleport request from "
                + RED + sender.getName()
                + RED + " denied.";
    }

    public static String requestCancelled() {
        return PREFIX + RED + "Teleport request cancelled.";
    }

    public static String requestExpired() {
        return PREFIX + RED + "Teleport request expired.";
    }

    public static String noPendingRequest() {
        return PREFIX + RED + "You do not have a pending teleport request.";
    }

    public static String playerNotFound() {
        return PREFIX + RED + "Player not found.";
    }

    public static String playerOffline(Player player) {
        return PREFIX
                + RED + player.getName()
                + " is no longer online.";
    }

    public static String teleportsDisabled(Player player) {
        return PREFIX
                + RED + player.getName()
                + RED + " has disabled teleport requests.";
    }

    public static String tpautoEnabled() {
        return PREFIX + WHITE + "TPAuto has been "
                + HIGHLIGHT + "enabled"
                + WHITE + ".";
    }

    public static String tpautoDisabled() {
        return PREFIX + WHITE + "TPAuto has been "
                + HIGHLIGHT + "disabled"
                + WHITE + ".";
    }

    public static String tptoggleEnabled() {
        return PREFIX + WHITE + "Teleport requests have been "
                + HIGHLIGHT + "disabled"
                + WHITE + ".";
    }

    public static String tptoggleDisabled() {
        return PREFIX + WHITE + "Teleport requests have been "
                + HIGHLIGHT + "enabled"
                + WHITE + ".";
    }

    public static String noPermission() {
        return PREFIX + RED + "You do not have permission to do that.";
    }

    public static String invalidUsage(String usage) {
        return PREFIX + RED + "Usage: " + usage;
    }

    public static String invalidCoordinates() {
        return PREFIX + RED + "Invalid coordinates.";
    }

    public static String selfTargeting() {
        return PREFIX + RED + "You cannot teleport to yourself.";
    }

    public static String selectorMustSelectOnePlayer() {
        return PREFIX + RED + "That selector must select exactly one player.";
    }
    public static String requestSentToTeleportToYou(
            Player target
    ) {
        return PREFIX
                + WHITE + "Teleport request sent to "
                + HIGHLIGHT + target.getName()
                + WHITE + " to teleport to you.";
    }

    public static String requestReceivedToYou(
            Player requester
    ) {
        return PREFIX
                + HIGHLIGHT + requester.getName()
                + WHITE + " has requested you to teleport to them.";
    }

    public static String requestCancelledByRequester(
            Player requester
    ) {
        return PREFIX
                + RED + "Teleport request from "
                + RED + requester.getName()
                + RED + " was cancelled.";
    }

    public static String requestAutoAccepted(Player target) {
        return PREFIX
                + WHITE + "Your teleport request to "
                + HIGHLIGHT + target.getName()
                + WHITE + " was automatically accepted.";
    }

    public static String spectating(Player target) {
        return PREFIX
                + WHITE + "You are now spectating "
                + HIGHLIGHT + target.getName()
                + WHITE + ".";
    }

    public static String spectateStopped() {
        return PREFIX
                + WHITE + "You are no longer spectating.";
    }
}