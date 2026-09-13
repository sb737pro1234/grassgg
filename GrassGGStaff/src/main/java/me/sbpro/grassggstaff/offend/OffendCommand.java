package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.punishment.PunishmentManager;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public final class OffendCommand implements CommandExecutor, TabCompleter {

    private final GrassGGStaff plugin;

    public OffendCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("grassgg.staff")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Messages.OFFEND_USAGE);
            return true;
        }

        String playerName = args[0];

        String reasonInput = String.join(
                " ",
                Arrays.copyOfRange(args, 1, args.length)
        );

        PunishmentReason reason =
                plugin.getReasonManager()
                        .find(reasonInput)
                        .orElse(null);

        if (reason == null) {
            sender.sendMessage(Messages.REASON_NOT_FOUND);
            return true;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(playerName);

        /*
         * We only accept players who are currently online or
         * who have previously joined the server.
         */
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(Messages.UNKNOWN_PLAYER);
            return true;
        }

        UUID uuid = target.getUniqueId();

        String latestName =
                target.getName() != null
                        ? target.getName()
                        : playerName;

        UUID staffUuid = null;
        String staffName = sender.getName();

        if (sender instanceof Player player) {
            staffUuid = player.getUniqueId();
        } else {
            /*
             * Console/offline staff UUID is represented by the
             * zero UUID rather than null because the database
             * requires a staff UUID.
             */
            staffUuid = new UUID(0L, 0L);
            staffName = "Console";
        }

        final UUID finalStaffUuid = staffUuid;
        final String finalStaffName = staffName;

        PunishmentManager manager =
                plugin.getPunishmentManager();

        manager.issue(
                        uuid,
                        latestName,
                        reason,
                        finalStaffUuid,
                        finalStaffName
                )
                .whenComplete((result, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (throwable != null) {
                                    plugin.getLogger().severe(
                                            "Database error while issuing punishment to "
                                                    + latestName
                                                    + ": "
                                                    + throwable.getMessage()
                                    );

                                    sender.sendMessage(
                                            Messages.DATABASE_ERROR
                                    );

                                    return;
                                }

                                if (!result.success()) {

                                    sender.sendMessage(
                                            Messages.INVALID_PUNISHMENT
                                    );

                                    plugin.getLogger().severe(
                                            "Could not punish "
                                                    + latestName
                                                    + ": "
                                                    + result.error()
                                    );

                                    return;
                                }

                                var record = result.record();

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

                                for (String message :
                                        Messages.punishmentIssued(
                                                record.playerName(),
                                                record.reasonName(),
                                                plugin.getPunishmentConfig()
                                                        .getTier(record.tier())
                                                        .orElseThrow(),
                                                record.offenceNumber(),
                                                new me.sbpro.grassggstaff.punishment.Punishment(
                                                        record.punishmentType(),
                                                        record.duration()
                                                )
                                        )
                                ) {
                                    sender.sendMessage(message);
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
                                                + record.duration()
                                                + " "
                                                + record.punishmentType()
                                );
                            }
                    );
                });

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (!sender.hasPermission("grassgg.staff")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {

            String input = args[0].toLowerCase(Locale.ROOT);

            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(name ->
                            name.toLowerCase(Locale.ROOT)
                                    .startsWith(input)
                    )
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length >= 2) {

            String current =
                    String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    1,
                                    args.length
                            )
                    ).toLowerCase(Locale.ROOT);

            return plugin.getReasonManager()
                    .all()
                    .stream()
                    .map(PunishmentReason::displayName)
                    .filter(name ->
                            name.toLowerCase(Locale.ROOT)
                                    .startsWith(current)
                    )
                    .sorted()
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}