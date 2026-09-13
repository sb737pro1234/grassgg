package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.punishment.OffenceRecord;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class OffenceRemoveCommand {

    private final GrassGGStaff plugin;

    public OffenceRemoveCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
    }

    public boolean execute(
            CommandSender sender,
            String[] args
    ) {

        if (!sender.hasPermission(
                "grassgg.staff.remove"
        )) {

            sender.sendMessage(
                    Messages.NO_PERMISSION
            );

            return true;
        }

        if (args.length != 2) {

            sender.sendMessage(
                    Messages.OFFENCE_REMOVE_USAGE
            );

            return true;
        }

        String playerName = args[1];

        OfflinePlayer player =
                Bukkit.getOfflinePlayer(playerName);

        if (!player.hasPlayedBefore() &&
                !player.isOnline()) {

            sender.sendMessage(
                    Messages.UNKNOWN_PLAYER
            );

            return true;
        }

        UUID uuid =
                player.getUniqueId();

        String name =
                player.getName() != null
                        ? player.getName()
                        : playerName;

        plugin.getPunishmentManager()
                .removeActivePunishment(uuid)
                .whenComplete((removed, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (throwable != null) {

                                    plugin.getLogger().warning(
                                            "Failed to remove punishment from "
                                                    + name
                                                    + ": "
                                                    + throwable.getMessage()
                                    );

                                    sender.sendMessage(
                                            Messages.DATABASE_ERROR
                                    );

                                    return;
                                }

                                if (removed == null) {

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
                                        Messages.PUNISHMENT_REMOVED
                                                .replace(
                                                        "{player}",
                                                        name
                                                )
                                                .replace(
                                                        "{offence}",
                                                        String.valueOf(
                                                                removed.offenceId()
                                                        )
                                                )
                                );

                                Player online =
                                        Bukkit.getPlayer(uuid);

                                if (online != null) {

                                    /*
                                     * The enforcement manager/cache
                                     * should already have been
                                     * invalidated by the manager.
                                     */
                                    plugin.getEnforcementManager()
                                            .invalidate(uuid);
                                }

                                plugin.getLogger().info(
                                        sender.getName()
                                                + " removed offence #"
                                                + removed.offenceId()
                                                + " from "
                                                + name
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
                "grassgg.staff.remove"
        )) {
            return List.of();
        }

        if (args.length != 2) {
            return List.of();
        }

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
}