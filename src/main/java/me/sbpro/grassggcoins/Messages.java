package me.sbpro.grassggcoins;

import me.sbpro.grassggcoins.util.AmountFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;

import java.util.List;

/**
 * All player-facing GrassGGCoins messages, titles, colours and GUI text live here.
 * Edit this class when you want to change the wording or the colour scheme.
 */
public final class Messages {

    private Messages() {
    }

    // =============================================================
    // Colours
    // =============================================================

    /** Main text colour: white. */
    public static final TextColor MAIN_COLOR = TextColor.color(0xFFFFFF);

    /** Change this one value to change highlight/title colour everywhere. */
    public static final TextColor CUSTOM_COLOR = TextColor.color(0x55FF55);

    // =============================================================
    // Prefix / general messages
    // =============================================================

    public static Component prefix() {
        return Component.text("GRASS.GG COINS ").color(CUSTOM_COLOR)
                .append(main("» "));
    }

    public static Component noPermission() {
        return prefix().append(main("You do not have permission to do that."));
    }

    public static Component playerOnly() {
        return prefix().append(main("This command can only be used by a player."));
    }

    public static Component invalidNumber() {
        return prefix().append(main("Please enter a valid whole number."));
    }

    public static Component invalidAmount() {
        return prefix().append(main("The amount must be zero or greater."));
    }

    public static Component playerNotFound() {
        return prefix().append(main("That player could not be found."));
    }

    public static Component unknownSubcommand() {
        return prefix().append(main("Unknown subcommand."));
    }

    // =============================================================
    // Reward messages
    // =============================================================

    public static Component rewardActionBar() {
        return highlight("You have received +1 coin.");
    }

    public static Component rewardChatMessage() {
        return main("-----------------------------------------------")
                .appendNewline()
                .append(highlight("You have received +1 coin."))
                .append(main(" Do /coins for more info."))
                .appendNewline()
                .append(main("-----------------------------------------------"));
    }

    // =============================================================
    // /coins menu
    // =============================================================

    public static Component coinsMenuTitle() {
        return highlight("COINS").decorate(net.kyori.adventure.text.format.TextDecoration.BOLD);
    }

    public static Component shopButtonName() {
        return highlight("Coin Shop").decorate(net.kyori.adventure.text.format.TextDecoration.BOLD);
    }

    public static List<Component> shopButtonLore() {
        return List.of(main("Click to go to the shop."));
    }

    public static Component balanceItemName(long balance) {
        return main("Coins: ").append(highlight(AmountFormatter.format(balance)));
    }

    public static List<Component> balanceItemLore() {
        return List.of(main("This is your current coin balance."));
    }

    public static Component infoItemName() {
        return highlight("What are coins?").decorate(net.kyori.adventure.text.format.TextDecoration.BOLD);
    }

    public static List<Component> infoItemLore() {
        return List.of(
                main("Earn coins through playing."),
                main("More ways to earn coins can be added later.")
        );
    }

    // =============================================================
    // /coinshop menu
    // =============================================================

    public static Component shopMenuTitle() {
        return highlight("COIN SHOP").decorate(net.kyori.adventure.text.format.TextDecoration.BOLD);
    }

    public static List<Component> shopProductLore(long cost) {
        return List.of(
                main("Cost: ").append(highlight(AmountFormatter.format(cost))).append(main(" coins")),
                main("Click to purchase.")
        );
    }

    public static Component shopEmptyItemName() {
        return main("Not configured");
    }

    public static List<Component> shopEmptyItemLore() {
        return List.of(main("Set this item's display item using the command."));
    }

    public static Component purchased(String identifier, long cost) {
        return prefix()
                .append(highlight("Purchase successful! "))
                .append(main("You bought "))
                .append(highlight(identifier))
                .append(main(" for "))
                .append(highlight(AmountFormatter.format(cost)))
                .append(main(" coin" + (cost == 1 ? "." : "s.")));
    }

    public static Component insufficientCoins(long balance, long cost) {
        return prefix()
                .append(highlight("You do not have enough coins."))
                .appendNewline()
                .append(main("Balance: "))
                .append(highlight(AmountFormatter.format(balance)))
                .append(main(" | Cost: "))
                .append(highlight(AmountFormatter.format(cost)));
    }

    public static final Sound INSUFFICIENT_COINS_SOUND = Sound.ENTITY_VILLAGER_NO;
    public static final float INSUFFICIENT_COINS_SOUND_VOLUME = 1.0f;
    public static final float INSUFFICIENT_COINS_SOUND_PITCH = 1.0f;

    public static Component purchaseCommandFailed() {
        return prefix().append(main("This shop item could not be processed, so your coins were not taken."));
    }

    // =============================================================
    // Admin command messages
    // =============================================================

    public static Component giveSuccess(String playerName, long amount) {
        return prefix().append(main("Gave "))
                .append(highlight(AmountFormatter.format(amount)))
                .append(main(" coin" + (amount == 1 ? "" : "s") + " to "))
                .append(highlight(playerName)).append(main("."));
    }

    public static Component takeSuccess(String playerName, long amount) {
        return prefix().append(main("Took "))
                .append(highlight(AmountFormatter.format(amount)))
                .append(main(" coin" + (amount == 1 ? "" : "s") + " from "))
                .append(highlight(playerName)).append(main("."));
    }

    public static Component setSuccess(String playerName, long amount) {
        return prefix().append(main("Set "))
                .append(highlight(playerName))
                .append(main("'s balance to "))
                .append(highlight(AmountFormatter.format(amount))).append(main("."));
    }

    public static Component balanceMessage(String playerName, long amount) {
        return prefix().append(highlight(playerName))
                .append(main(" has "))
                .append(highlight(AmountFormatter.format(amount)))
                .append(main(" coin" + (amount == 1 ? "." : "s.")));
    }

    public static Component setDisplaySuccess(String identifier) {
        return prefix().append(main("Set the display item for "))
                .append(highlight(identifier)).append(main("."));
    }

    public static Component shopIdentifierMissing() {
        return prefix().append(main("Please provide a shop identifier."));
    }

    public static Component shopIdentifierUnknown(String identifier) {
        return prefix().append(main("No shop item exists with identifier "))
                .append(highlight(identifier)).append(main("."));
    }

    public static Component emptyHand() {
        return prefix().append(main("You must hold the display item in your main hand."));
    }

    public static Component usage(String usage) {
        return prefix().append(main("Usage: ")).append(highlight(usage));
    }

    // =============================================================
    // Component helpers
    // =============================================================

    public static Component main(String text) {
        return Component.text(text).color(MAIN_COLOR);
    }

    public static Component highlight(String text) {
        return Component.text(text).color(CUSTOM_COLOR);
    }
}
