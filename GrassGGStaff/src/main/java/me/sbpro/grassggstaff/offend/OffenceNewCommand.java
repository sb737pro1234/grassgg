package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import me.sbpro.grassggstaff.util.PlayerLookup;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class OffenceNewCommand {

    private final GrassGGStaff plugin;

    public OffenceNewCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
    }

    public boolean execute(
            CommandSender sender,
            String[] args
    ) {

        if (args.length < 3) {

            sender.sendMessage(
                    Messages.OFFENCE_NEW_USAGE
            );

            return true;
        }

        String playerName = args[1];

        String reasonInput =
                String.join(
                        " ",
                        Arrays.copyOfRange(
                                args,
                                2,
                                args.length
                        )
                );

        PunishmentReason reason =
                plugin.getReasonManager()
                        .find(reasonInput)
                        .orElse(null);

        if (reason == null) {

            sender.sendMessage(
                    Messages.REASON_NOT_FOUND
            );

            return true;
        }

        PlayerLookup.find(
                plugin,
                playerName
        ).whenComplete((result, throwable) -> {

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

                        if (!result.found()) {

                            sender.sendMessage(
                                    Messages.UNKNOWN_PLAYER
                            );

                            return;
                        }

                        UUID staffUuid =
                                new UUID(0L, 0L);

                        String staffName =
                                "Console";

                        if (sender instanceof Player player) {
                            staffUuid =
                                    player.getUniqueId();

                            staffName =
                                    player.getName();
                        }

                        UUID finalStaffUuid =
                                staffUuid;

                        String finalStaffName =
                                staffName;

                        plugin.getPunishmentManager()
                                .issue(
                                        result.uuid(),
                                        result.name(),
                                        reason,
                                        finalStaffUuid,
                                        finalStaffName
                                )
                                .whenComplete(
                                        (punishmentResult,
                                         punishmentThrowable) -> {

                                            Bukkit.getScheduler().runTask(
                                                    plugin,
                                                    () -> {

                                                        if (punishmentThrowable != null) {

                                                            plugin.getLogger().warning(
                                                                    "Failed to issue offence to "
                                                                            + result.name()
                                                                            + ": "
                                                                            + punishmentThrowable.getMessage()
                                                            );

                                                            sender.sendMessage(
                                                                    Messages.DATABASE_ERROR
                                                            );

                                                            return;
                                                        }

                                                        if (!punishmentResult.success()) {

                                                            plugin.getLogger().warning(
                                                                    "Punishment failed for "
                                                                            + result.name()
                                                                            + ": "
                                                                            + punishmentResult.error()
                                                            );

                                                            sender.sendMessage(
                                                                    Messages.INVALID_PUNISHMENT
                                                            );

                                                            return;
                                                        }

                                                        var record =
                                                                punishmentResult.record();

                                                        // Apply the punishment immediately if the target is online.
                                                        if (record.punishmentType() != me.sbpro.grassggstaff.punishment.PunishmentType.WARNING) {
                                                            Player onlineTarget = Bukkit.getPlayer(record.playerUuid());
                                                            if (onlineTarget != null && onlineTarget.isOnline()) {
                                                                if (record.punishmentType() == me.sbpro.grassggstaff.punishment.PunishmentType.BAN) {
                                                                    onlineTarget.kickPlayer(Messages.banScreen(record));
                                                                } else {
                                                                    onlineTarget.kickPlayer(Messages.muteScreen(record));
                                                                }
                                                            }
                                                        }

                                                        var tier =
                                                                plugin.getPunishmentConfig()
                                                                        .getTier(
                                                                                record.tier()
                                                                        )
                                                                        .orElse(null);

                                                        if (tier == null) {

                                                            sender.sendMessage(
                                                                    Messages.INVALID_PUNISHMENT
                                                            );

                                                            return;
                                                        }

                                                        var punishment =
                                                                new me.sbpro.grassggstaff.punishment.Punishment(
                                                                        record.punishmentType(),
                                                                        record.duration()
                                                                );

                                                        for (String line :
                                                                Messages.punishmentIssued(
                                                                        record.playerName(),
                                                                        record.reasonName(),
                                                                        tier,
                                                                        record.offenceNumber(),
                                                                        punishment
                                                                )) {

                                                            sender.sendMessage(
                                                                    line
                                                            );
                                                        }

                                                        plugin.getLogger().info(
                                                                sender.getName()
                                                                        + " punished "
                                                                        + record.playerName()
                                                                        + " | Reason: "
                                                                        + record.reasonName()
                                                                        + " | Tier: "
                                                                        + record.tier()
                                                                        + " | Offence: "
                                                                        + record.offenceNumber()
                                                                        + " | Punishment: "
                                                                        + punishment.display()
                                                        );
                                                    }
                                            );
                                        }
                                );
                    }
            );
        });

        return true;
    }

    public List<String> tabComplete(
            CommandSender sender,
            String[] args
    ) {

        if (args.length == 2) {

            String input =
                    args[1].toLowerCase(Locale.ROOT);

            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(name ->
                            name.toLowerCase(Locale.ROOT)
                                    .startsWith(input)
                    )
                    .sorted()
                    .toList();
        }

        if (args.length >= 3) {

            String input =
                    String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    2,
                                    args.length
                            )
                    ).toLowerCase(Locale.ROOT);

            return plugin.getReasonManager()
                    .all()
                    .stream()
                    .map(PunishmentReason::displayName)
                    .filter(name ->
                            name.toLowerCase(Locale.ROOT)
                                    .startsWith(input)
                    )
                    .sorted()
                    .toList();
        }

        return List.of();
    }
}