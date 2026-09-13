package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.util.PlayerLookup;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public final class OffenceResetCommand {

    private final GrassGGStaff plugin;

    public OffenceResetCommand(
            GrassGGStaff plugin
    ) {
        this.plugin = plugin;
    }

    public boolean execute(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.staff.reset"
        )) {

            sender.sendMessage(
                    Messages.NO_PERMISSION
            );

            return true;
        }

        if (args.length != 2) {

            sender.sendMessage(
                    Messages.OFFENCE_RESET_USAGE
            );

            return true;
        }

        String playerName =
                args[1];

        PlayerLookup.find(
                plugin,
                playerName
        ).whenComplete(
                (lookup, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (throwable != null) {

                                    plugin.getLogger().warning(
                                            "Failed to look up player "
                                                    + playerName
                                                    + ": "
                                                    + throwable.getMessage()
                                    );

                                    sender.sendMessage(
                                            Messages.DATABASE_ERROR
                                    );

                                    return;
                                }

                                if (!lookup.found()) {

                                    sender.sendMessage(
                                            Messages.UNKNOWN_PLAYER
                                    );

                                    return;
                                }

                                plugin.getPunishmentManager()
                                        .resetPlayerHistory(
                                                lookup.uuid()
                                        )
                                        .whenComplete(
                                                (success,
                                                 resetThrowable) -> {

                                                    Bukkit.getScheduler()
                                                            .runTask(
                                                                    plugin,
                                                                    () -> {

                                                                        if (resetThrowable != null) {

                                                                            plugin.getLogger().warning(
                                                                                    "Failed to reset punishment history for "
                                                                                            + lookup.name()
                                                                                            + ": "
                                                                                            + resetThrowable.getMessage()
                                                                            );

                                                                            sender.sendMessage(
                                                                                    Messages.DATABASE_ERROR
                                                                            );

                                                                            return;
                                                                        }

                                                                        if (!success) {

                                                                            sender.sendMessage(
                                                                                    Messages.RESET_FAILED
                                                                                            .replace(
                                                                                                    "{player}",
                                                                                                    lookup.name()
                                                                                            )
                                                                            );

                                                                            return;
                                                                        }

                                                                        plugin.getEnforcementManager()
                                                                                .invalidate(
                                                                                        lookup.uuid()
                                                                                );

                                                                        sender.sendMessage(
                                                                                Messages.PUNISHMENT_HISTORY_RESET
                                                                                        .replace(
                                                                                                "{player}",
                                                                                                lookup.name()
                                                                                        )
                                                                        );

                                                                        plugin.getLogger().info(
                                                                                sender.getName()
                                                                                        + " reset the punishment history of "
                                                                                        + lookup.name()
                                                                        );
                                                                    }
                                                            );
                                                }
                                        );
                            }
                    );
                }
        );

        return true;
    }

    public List<String> tabComplete(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.staff.reset"
        )) {
            return List.of();
        }

        if (args.length != 2) {
            return List.of();
        }

        String input =
                args[1].toLowerCase(
                        Locale.ROOT
                );

        return Bukkit.getOnlinePlayers()
                .stream()
                .map(
                        Player::getName
                )
                .filter(
                        name ->
                                name.toLowerCase(
                                                Locale.ROOT
                                        )
                                        .startsWith(
                                                input
                                        )
                )
                .sorted()
                .toList();
    }
}