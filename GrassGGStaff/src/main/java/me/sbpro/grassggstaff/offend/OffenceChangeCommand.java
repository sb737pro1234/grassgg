package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import me.sbpro.grassggstaff.punishment.OffenceRecord;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class OffenceChangeCommand {

    private final GrassGGStaff plugin;

    public OffenceChangeCommand(
            GrassGGStaff plugin
    ) {
        this.plugin = plugin;
    }

    public boolean execute(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.staff.change"
        )) {

            sender.sendMessage(
                    Messages.NO_PERMISSION
            );

            return true;
        }

        if (args.length < 3) {

            sender.sendMessage(
                    Messages.OFFENCE_CHANGE_USAGE
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

        OfflinePlayer player =
                Bukkit.getOfflinePlayer(playerName);

        if (!player.hasPlayedBefore() &&
                !player.isOnline()) {

            sender.sendMessage(
                    Messages.UNKNOWN_PLAYER
            );

            return true;
        }

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

        UUID uuid =
                player.getUniqueId();

        String name =
                player.getName() != null
                        ? player.getName()
                        : playerName;

        UUID staffUuid;
        String staffName;

        if (sender instanceof Player staff) {
            staffUuid = staff.getUniqueId();
            staffName = staff.getName();
        } else {
            staffUuid = new UUID(0L, 0L);
            staffName = "Console";
        }

        plugin.getPunishmentManager()
                .changeActiveReason(
                        uuid,
                        reason,
                        staffUuid,
                        staffName
                )
                .whenComplete((result, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (throwable != null) {

                                    plugin.getLogger().warning(
                                            "Failed to change offence for "
                                                    + name
                                                    + ": "
                                                    + throwable.getMessage()
                                    );

                                    sender.sendMessage(
                                            Messages.DATABASE_ERROR
                                    );

                                    return;
                                }

                                if (result == null) {

                                    sender.sendMessage(
                                            Messages.NO_ACTIVE_PUNISHMENT
                                                    .replace(
                                                            "{player}",
                                                            name
                                                    )
                                    );

                                    return;
                                }

                                sender.sendMessage(
                                        Messages.PUNISHMENT_CHANGED
                                                .replace(
                                                        "{player}",
                                                        name
                                                )
                                                .replace(
                                                        "{reason}",
                                                        result.reasonName()
                                                )
                                                .replace(
                                                        "{tier}",
                                                        result.tier()
                                                )
                                                .replace(
                                                        "{offence}",
                                                        String.valueOf(
                                                                result.offenceNumber()
                                                        )
                                                )
                                );

                                plugin.getLogger().info(
                                        sender.getName()
                                                + " changed the active punishment "
                                                + "for "
                                                + name
                                                + " to "
                                                + result.reasonName()
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

        if (!sender.hasPermission(
                "grassgg.staff.change"
        )) {
            return List.of();
        }

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
                    .filter(reason ->
                            reason.toLowerCase(Locale.ROOT)
                                    .startsWith(input)
                    )
                    .sorted()
                    .toList();
        }

        return List.of();
    }
}