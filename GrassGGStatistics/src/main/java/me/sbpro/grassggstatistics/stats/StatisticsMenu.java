package me.sbpro.grassggstatistics.stats;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class StatisticsMenu {

    private static final int BUTTON_WIDTH = 180;

    public static void open(Player viewer, Player target) {
        viewer.showDialog(createMainDialog(viewer, target));
    }

    private static Dialog createMainDialog(Player viewer, Player target) {
        String title = viewer.equals(target)
                ? "Statistics"
                : "Statistics for " + target.getName();

        DialogBase base = DialogBase.builder(Component.text(title))
                .canCloseWithEscape(true)
                .pause(false)
                .afterAction(DialogAfterAction.NONE)
                .body(List.of(
                        DialogBody.plainMessage(
                                createPlayerSummary(target),
                                350
                        )
                ))
                .build();

        return createDialog(base, List.of(
                categoryButton("Combat", "Player kills, mob kills and deaths",
                        p -> p.showDialog(createCombatDialog(viewer, target))),
                categoryButton("Mining", "Mining and block-breaking statistics",
                        p -> p.showDialog(createMiningDialog(viewer, target))),
                categoryButton("Building", "Building and crafting statistics",
                        p -> p.showDialog(createBuildingDialog(viewer, target))),
                categoryButton("Exploration", "Travel and exploration statistics",
                        p -> p.showDialog(createExplorationDialog(viewer, target))),
                categoryButton("Farming", "Farming and animal statistics",
                        p -> p.showDialog(createFarmingDialog(viewer, target))),
                categoryButton("Fishing", "Fishing statistics",
                        p -> p.showDialog(createFishingDialog(viewer, target))),
                categoryButton("General", "General player statistics",
                        p -> p.showDialog(createGeneralDialog(viewer, target)))
        ), createCloseButton());
    }

    private static Dialog createCombatDialog(Player viewer, Player target) {
        DialogBase base = createBase("Combat", "Combat statistics for " + target.getName());
        return createStatisticDialog(base, "Combat", List.of(
                "Player Kills » " + formatNumber(target.getStatistic(Statistic.PLAYER_KILLS)),
                "Mob Kills » " + formatNumber(target.getStatistic(Statistic.MOB_KILLS)),
                "Deaths » " + formatNumber(target.getStatistic(Statistic.DEATHS))
        ), createBackButton(viewer, target));
    }

    private static Dialog createMiningDialog(Player viewer, Player target) {
        DialogBase base = createBase("Mining", "Mining statistics for " + target.getName());
        return createStatisticDialog(base, "Mining", List.of(
                "Blocks Broken » " + formatNumber(StatisticsFormatter.getBlocksBroken(target)),
                "Ore Blocks Mined » " + formatNumber(StatisticsFormatter.getOreBlocksMined(target)),
                "Tools Broken » " + formatNumber(StatisticsFormatter.getToolsBroken(target))
        ), createBackButton(viewer, target));
    }

    private static Dialog createBuildingDialog(Player viewer, Player target) {
        DialogBase base = createBase("Building", "Building statistics for " + target.getName());
        return createStatisticDialog(base, "Building", List.of(
                "Blocks Placed » " + formatNumber(StatisticsFormatter.getBlocksPlaced(target)),
                "Items Crafted » " + formatNumber(StatisticsFormatter.getItemsCrafted(target))
        ), createBackButton(viewer, target));
    }

    private static Dialog createExplorationDialog(Player viewer, Player target) {
        DialogBase base = createBase("Exploration", "Exploration statistics for " + target.getName());
        return createStatisticDialog(base, "Exploration", List.of(
                "Distance Travelled » " + StatisticsFormatter.formatDistance(
                        StatisticsFormatter.getTotalDistance(target)),
                "Jumps » " + formatNumber(target.getStatistic(Statistic.JUMP))
        ), createBackButton(viewer, target));
    }

    private static Dialog createFarmingDialog(Player viewer, Player target) {
        DialogBase base = createBase("Farming", "Farming statistics for " + target.getName());
        return createStatisticDialog(base, "Farming", List.of(
                "Crops Harvested » " + formatNumber(StatisticsFormatter.getCropsHarvested(target)),
                "Animals Bred » " + formatNumber(target.getStatistic(Statistic.ANIMALS_BRED))
        ), createBackButton(viewer, target));
    }

    private static Dialog createFishingDialog(Player viewer, Player target) {
        DialogBase base = createBase("Fishing", "Fishing statistics for " + target.getName());
        return createStatisticDialog(base, "Fishing", List.of(
                "Fish Caught » " + formatNumber(target.getStatistic(Statistic.FISH_CAUGHT))
        ), createBackButton(viewer, target));
    }

    private static Dialog createGeneralDialog(Player viewer, Player target) {
        DialogBase base = createBase("General", "General statistics for " + target.getName());
        return createStatisticDialog(base, "General", List.of(
                "Playtime » " + StatisticsFormatter.formatPlaytime(
                        target.getStatistic(Statistic.PLAY_ONE_MINUTE)),
                "Times Slept » " + formatNumber(target.getStatistic(Statistic.SLEEP_IN_BED)),
                "Jumps » " + formatNumber(target.getStatistic(Statistic.JUMP))
        ), createBackButton(viewer, target));
    }

    private static Dialog createStatisticDialog(
            DialogBase base,
            String categoryTitle,
            List<String> statistics,
            ActionButton backButton
    ) {

        // Title: BOLD + UNDERLINED only
        Component categoryTitleComponent = Component.text(categoryTitle)
                .decorate(TextDecoration.BOLD)
                .decorate(TextDecoration.UNDERLINED);

        // Statistics: explicitly NOT bold or underlined
        Component statisticText = Component.text("")
                .append(categoryTitleComponent)
                .append(Component.text("\n\n"));

        for (int i = 0; i < statistics.size(); i++) {

            statisticText = statisticText.append(
                    Component.text(statistics.get(i))
                            .decoration(TextDecoration.BOLD, false)
                            .decoration(TextDecoration.UNDERLINED, false)
            );

            if (i < statistics.size() - 1) {
                statisticText = statisticText.append(Component.text("\n"));
            }
        }

        final Component finalStatisticText = statisticText;

        return Dialog.create(factory -> {

            var builder = factory.empty();

            builder.base(
                    DialogBase.builder(base.title())
                            .canCloseWithEscape(true)
                            .pause(false)
                            .afterAction(DialogAfterAction.NONE)
                            .body(List.of(
                                    DialogBody.plainMessage(
                                            finalStatisticText,
                                            350
                                    )
                            ))
                            .build()
            );

            builder.type(
                    DialogType.multiAction(
                            List.of(backButton),
                            null,
                            1
                    )
            );
        });
    }


    private static Dialog createDialog(
            DialogBase base,
            List<ActionButton> buttons,
            ActionButton exitButton
    ) {
        return Dialog.create(factory -> {
            var builder = factory.empty();
            builder.base(base);
            builder.type(DialogType.multiAction(buttons, exitButton, 2));
        });
    }

    private static DialogBase createBase(String title, String description) {
        return DialogBase.builder(Component.text(title))
                .canCloseWithEscape(true)
                .pause(false)
                .afterAction(DialogAfterAction.NONE)
                .body(List.of(DialogBody.plainMessage(Component.text(description), 350)))
                .build();
    }

    private static ActionButton categoryButton(
            String title,
            String description,
            Consumer<Player> action
    ) {
        return ActionButton.create(
                Component.text(title),
                Component.text(description),
                BUTTON_WIDTH,
                DialogAction.customClick(
                        (response, audience) -> {
                            if (audience instanceof Player player) {
                                action.accept(player);
                            }
                        },
                        ClickCallback.Options.builder()
                                .uses(ClickCallback.UNLIMITED_USES)
                                .build()
                )
        );
    }

    private static ActionButton createBackButton(Player viewer, Player target) {
        return ActionButton.create(
                Component.text("Back"),
                Component.text("Return to Statistics"),
                BUTTON_WIDTH,
                DialogAction.customClick(
                        (response, audience) -> {
                            if (audience instanceof Player player) {
                                player.showDialog(createMainDialog(viewer, target));
                            }
                        },
                        ClickCallback.Options.builder()
                                .uses(ClickCallback.UNLIMITED_USES)
                                .build()
                )
        );
    }

    private static ActionButton createCloseButton() {
        return ActionButton.create(
                Component.text("Close"),
                Component.text("Close statistics"),
                BUTTON_WIDTH,
                null
        );
    }

    private static String formatNumber(long number) {
        return StatisticsFormatter.formatNumber(number);
    }

    private static Component createPlayerSummary(Player player) {

        Component summary = Component.text("Username: ")
                .append(Component.text(player.getName()))
                .append(Component.text("\n"))

                .append(Component.text("Rank: "))
                .append(getPrefixComponent(player))
                .append(Component.text("\n"))

                .append(Component.text("Balance: "))
                .append(Component.text(getBalance(player)))
                .append(Component.text("\n"))

                .append(Component.text("Playtime: "))
                .append(Component.text(
                        StatisticsFormatter.formatPlaytime(
                                player.getStatistic(Statistic.PLAY_ONE_MINUTE)
                        )
                ))
                .append(Component.text("\n"))

                .append(Component.text("First Join: "))
                .append(Component.text(
                        StatisticsFormatter.formatDate(
                                player.getFirstPlayed()
                        )
                ));

        return summary;
    }

    private static String getBalance(Player player) {
        try {
            var registration = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (registration == null) return "Unknown";

            return "$" + StatisticsFormatter.formatNumber(
                    Math.round(registration.getProvider().getBalance((OfflinePlayer) player))
            );
        } catch (Exception ex) {
            return "Unknown";
        }
    }

    private static Component getPrefixComponent(Player player) {

        try {

            var registration = Bukkit.getServicesManager()
                    .getRegistration(LuckPerms.class);

            if (registration == null) {
                return Component.text("Unknown");
            }

            LuckPerms api = registration.getProvider();

            User user = api.getUserManager()
                    .getUser(player.getUniqueId());

            if (user == null) {
                return Component.text("Unknown");
            }

            CachedMetaData meta = user.getCachedData().getMetaData();

            String prefix = meta.getPrefix();

            if (prefix == null || prefix.isEmpty()) {
                return Component.text("Unknown");
            }

            return parsePrefix(prefix);

        } catch (Exception ex) {

            return Component.text("Unknown");
        }
    }
    private static Component parsePrefix(String prefix) {

        // MiniMessage prefixes, including gradients
        if (prefix.contains("<") && prefix.contains(">")) {

            try {

                return net.kyori.adventure.text.minimessage.MiniMessage
                        .miniMessage()
                        .deserialize(prefix);

            } catch (Exception ignored) {
                // Fall through to legacy parsing
            }
        }

        // Legacy / hex colour prefixes
        return net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                .legacySection()
                .deserialize(
                        colorize(prefix)
                );
    }

    private static String colorize(String text) {
        if (text == null) return "Unknown";

        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("&#([A-Fa-f0-9]{6})")
                .matcher(text);

        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");

            for (char c : color.toCharArray()) {
                replacement.append('§').append(c);
            }

            matcher.appendReplacement(builder, replacement.toString());
        }

        matcher.appendTail(builder);

        return org.bukkit.ChatColor.translateAlternateColorCodes('&', builder.toString());
    }
}
