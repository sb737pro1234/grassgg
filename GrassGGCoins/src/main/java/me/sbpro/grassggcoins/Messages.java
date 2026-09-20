package me.sbpro.grassggcoins;

import me.sbpro.grassggcoins.util.AmountFormatter;
import org.bukkit.Sound;

import java.util.List;

/**
 * All player-facing GrassGGCoins messages, titles, colours and GUI text live here.
 *
 * This class intentionally uses the classic Bukkit String-based message API rather
 * than Adventure Components, so all messages can be edited using normal § colour
 * codes, including the §x hex format.
 */
public final class Messages {

    private Messages() {
    }

    // =============================================================
    // Colours
    // =============================================================

    /** Main text colour: white. */
    public static final String WHITE = "§f";

    /**
     * Main GrassGGCoins highlight/title colour.
     * Change this one value to change the highlight colour everywhere.
     * Current colour: #FFD54A
     */
    public static final String CUSTOM = "§x§F§F§D§5§4§A";

    /** Error colour: red. */
    public static final String ERROR = "§c";

    // =============================================================
    // Prefix / general messages
    // =============================================================

    public static String prefix() {
        return CUSTOM + "§lCOINS §8» §r";
    }

    public static String noPermission() {
        return prefix() + ERROR + "You do not have permission to do that.";
    }

    public static String playerOnly() {
        return prefix() + ERROR + "Only players can use this command.";
    }

    public static String invalidNumber() {
        return prefix() + ERROR + "Please enter a valid whole number.";
    }

    public static String invalidAmount() {
        return prefix() + ERROR + "The amount must be zero or greater.";
    }

    public static String playerNotFound() {
        return prefix() + ERROR + "That player could not be found.";
    }

    public static String unknownSubcommand() {
        return prefix() + ERROR + "Unknown subcommand.";
    }

    // =============================================================
    // Reward messages
    // =============================================================

    public static String rewardActionBar() {
        return WHITE + "You have received " + CUSTOM + "+1 coin" + WHITE +".";
    }

    public static String rewardChatMessage() {
        return "§8-----------------------------------------------\n"
                + prefix() + WHITE + "You have received " + CUSTOM + "+1 coin" + WHITE + "."
                + WHITE + " Do " + CUSTOM + "/coins" + WHITE + " for more info.\n"
                + "§8-----------------------------------------------";
    }

    // =============================================================
    // /coins menu
    // =============================================================

    public static String coinsMenuTitle() {
        return CUSTOM + "§lCOINS";
    }

    public static String shopButtonName() {
        return CUSTOM + "§lCoin Shop";
    }

    public static List<String> shopButtonLore() {
        return List.of(
                WHITE + "Click to go to the shop."
        );
    }

    public static String balanceItemName(long balance) {
        return WHITE + "Coins: "
                + CUSTOM
                + AmountFormatter.format(balance);
    }

    public static List<String> balanceItemLore() {
        return List.of(
                WHITE + "This is your current coin balance."
        );
    }

    public static String infoItemName() {
        return CUSTOM + "§lWhat are coins?";
    }

    public static List<String> infoItemLore() {
        return List.of(
                WHITE + "Earn coins through playing.",
                WHITE + "More ways to earn coins can be added later."
        );
    }

    // =============================================================
    // /coinshop menu
    // =============================================================

    public static String shopMenuTitle() {
        return CUSTOM + "§lCOIN SHOP";
    }

    public static List<String> shopProductLore(long cost) {
        return List.of(
                WHITE + "Cost: "
                        + CUSTOM
                        + AmountFormatter.format(cost)
                        + WHITE
                        + " coins",
                WHITE + "Click to purchase."
        );
    }

    public static String shopEmptyItemName() {
        return WHITE + "Not configured";
    }

    public static List<String> shopEmptyItemLore() {
        return List.of(
                WHITE + "Set this item's display item using the command."
        );
    }

    public static String purchased(String identifier, long cost) {
        return prefix()
                + CUSTOM + "Purchase successful! "
                + WHITE + "You bought "
                + CUSTOM + identifier
                + WHITE + " for "
                + CUSTOM + AmountFormatter.format(cost)
                + WHITE + " coin"
                + (cost == 1 ? "." : "s.");
    }

    public static String insufficientCoins(long balance, long cost) {
        return prefix()
                + ERROR + "You do not have enough coins."
                + "\n"
                + WHITE + "Balance: "
                + CUSTOM + AmountFormatter.format(balance)
                + WHITE + " | Cost: "
                + CUSTOM + AmountFormatter.format(cost);
    }

    public static final Sound INSUFFICIENT_COINS_SOUND = Sound.ENTITY_VILLAGER_NO;
    public static final float INSUFFICIENT_COINS_SOUND_VOLUME = 1.0f;
    public static final float INSUFFICIENT_COINS_SOUND_PITCH = 1.0f;

    public static String purchaseCommandFailed() {
        return prefix()
                + ERROR
                + "This shop item could not be processed, so your coins were not taken.";
    }

    // =============================================================
    // Admin command messages
    // =============================================================

    public static String giveSuccess(String playerName, long amount) {
        return prefix()
                + WHITE + "Gave "
                + CUSTOM + AmountFormatter.format(amount)
                + WHITE + " coin"
                + (amount == 1 ? "" : "s")
                + " to "
                + CUSTOM + playerName
                + WHITE + ".";
    }

    public static String takeSuccess(String playerName, long amount) {
        return prefix()
                + WHITE + "Took "
                + CUSTOM + AmountFormatter.format(amount)
                + WHITE + " coin"
                + (amount == 1 ? "" : "s")
                + " from "
                + CUSTOM + playerName
                + WHITE + ".";
    }

    public static String setSuccess(String playerName, long amount) {
        return prefix()
                + WHITE + "Set "
                + CUSTOM + playerName
                + WHITE + "'s balance to "
                + CUSTOM + AmountFormatter.format(amount)
                + WHITE + ".";
    }

    public static String balanceMessage(String playerName, long amount) {
        return prefix()
                + CUSTOM + playerName
                + WHITE + " has "
                + CUSTOM + AmountFormatter.format(amount)
                + WHITE + " coin"
                + (amount == 1 ? "." : "s.");
    }

    public static String setDisplaySuccess(String identifier) {
        return prefix()
                + WHITE + "Set the display item for "
                + CUSTOM + identifier
                + WHITE + ".";
    }

    public static String shopIdentifierMissing() {
        return prefix()
                + ERROR
                + "Please provide a shop identifier.";
    }

    public static String shopIdentifierUnknown(String identifier) {
        return prefix()
                + ERROR
                + "No shop item exists with identifier "
                + CUSTOM + identifier
                + ERROR + ".";
    }

    public static String emptyHand() {
        return prefix()
                + ERROR
                + "You must hold the display item in your main hand.";
    }

    public static String usage(String usage) {
        return prefix()
                + ERROR
                + "Usage: "
                + CUSTOM + usage;
    }

    // =============================================================
// Purchase confirmation menu
// =============================================================

    public static String confirmationMenuTitle() {
        return CUSTOM + "§lCONFIRM PURCHASE";
    }

    public static String confirmPurchaseName() {
        return "§a§lConfirm";
    }

    public static List<String> confirmPurchaseLore(long cost) {
        return List.of(
                WHITE + "Click to confirm your purchase.",
                WHITE + "Cost: "
                        + CUSTOM
                        + AmountFormatter.format(cost)
                        + " coins"
        );
    }

    public static String cancelPurchaseName() {
        return ERROR + "§lCancel";
    }

    public static List<String> cancelPurchaseLore() {
        return List.of(
                WHITE + "Click to return to the shop."
        );
    }
}