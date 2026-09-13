package me.sbpro.grassggstaff.sus;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.reason.PunishmentReason;
import me.sbpro.grassggstaff.util.PlayerLookup;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class SusCommand implements CommandExecutor, TabCompleter {

    private final GrassGGStaff plugin;
    private final SusManager manager;

    public SusCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
        this.manager = plugin.getSusManager();
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        return handle(sender, args);
    }

    /**
     * Handles /offence sus ...
     *
     * The supplied args still contain "sus" at index 0.
     */
    public boolean executeFromOffence(
            CommandSender sender,
            String[] args
    ) {

        if (args.length == 0
                || !args[0].equalsIgnoreCase("sus")) {
            return false;
        }

        String[] susArgs = Arrays.copyOfRange(
                args,
                1,
                args.length
        );

        return handle(sender, susArgs);
    }

    private boolean handle(
            CommandSender sender,
            String[] args
    ) {

        /*
         * /sus
         * /offence sus
         *
         * Opens the SUS GUI.
         */
        if (args.length == 0) {

            if (!hasPermission(
                    sender,
                    "grassgg.staff.sus"
            )) {
                sender.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage(Messages.SUS_PLAYER_ONLY);
                return true;
            }

            refreshAndOpen(player);

            return true;
        }

        return switch (
                args[0].toLowerCase(Locale.ROOT)
                ) {

            case "add" ->
                    handleAdd(sender, args);

            case "remove" ->
                    handleRemove(sender, args);

            default -> {
                sender.sendMessage(Messages.SUS_USAGE);
                yield true;
            }
        };
    }

    private void refreshAndOpen(Player player) {

        manager.refreshAll()
                .whenComplete((ignored, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (!player.isOnline()) {
                                    return;
                                }

                                if (throwable != null) {
                                    plugin.getLogger().warning(
                                            "Failed to load SUS players: "
                                                    + getCauseMessage(throwable)
                                    );

                                    player.sendMessage(
                                            Messages.DATABASE_ERROR
                                    );

                                    return;
                                }

                                SusMenu.open(
                                        player,
                                        manager,
                                        0
                                );
                            }
                    );
                });
    }

    private boolean handleAdd(
            CommandSender sender,
            String[] args
    ) {

        if (!hasPermission(
                sender,
                "grassgg.staff.sus.add"
        )) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Messages.SUS_ADD_USAGE);
            return true;
        }

        String playerName = args[1];

        /*
         * Custom reasons are allowed.
         *
         * This means:
         * /sus add Steve X-Ray
         *
         * works even if "X-Ray" is not in punishments.yml.
         */
        String reason = String.join(
                " ",
                Arrays.copyOfRange(
                        args,
                        2,
                        args.length
                )
        ).trim();

        if (reason.isEmpty()) {
            sender.sendMessage(Messages.SUS_ADD_USAGE);
            return true;
        }

        if (reason.length() > 255) {
            sender.sendMessage(Messages.SUS_REASON_TOO_LONG);
            return true;
        }

        UUID staffUuid;
        String staffName;

        if (sender instanceof Player player) {

            staffUuid = player.getUniqueId();
            staffName = player.getName();

        } else {

            staffUuid = new UUID(0L, 0L);
            staffName = "Console";
        }

        lookupPlayer(playerName)
                .thenCompose(result -> {

                    if (!result.found()) {

                        return CompletableFuture.failedFuture(
                                new IllegalArgumentException(
                                        "PLAYER_NOT_FOUND"
                                )
                        );
                    }

                    return manager.addNote(
                            result.uuid(),
                            result.name(),
                            reason,
                            staffUuid,
                            staffName
                    );
                })
                .whenComplete((result, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (throwable != null) {

                                    Throwable cause =
                                            getCause(throwable);

                                    if ("PLAYER_NOT_FOUND".equals(
                                            cause.getMessage()
                                    )) {

                                        sender.sendMessage(
                                                Messages.UNKNOWN_PLAYER
                                        );

                                    } else {

                                        plugin.getLogger().warning(
                                                "Failed to add SUS for "
                                                        + playerName
                                                        + ": "
                                                        + cause.getMessage()
                                        );

                                        sender.sendMessage(
                                                Messages.DATABASE_ERROR
                                        );
                                    }

                                    return;
                                }

                                if (result.status()
                                        == SusManager.AddResult.Status.ALREADY_EXISTS) {

                                    sender.sendMessage(
                                            Messages.susAlreadyExists(
                                                    playerName,
                                                    reason
                                            )
                                    );

                                    return;
                                }

                                sender.sendMessage(
                                        Messages.susAdded(
                                                result.note()
                                        )
                                );
                            }
                    );
                });

        return true;
    }

    private boolean handleRemove(
            CommandSender sender,
            String[] args
    ) {

        if (!hasPermission(
                sender,
                "grassgg.staff.sus.remove"
        )) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Messages.SUS_REMOVE_USAGE);
            return true;
        }

        String playerName = args[1];

        String reason = String.join(
                " ",
                Arrays.copyOfRange(
                        args,
                        2,
                        args.length
                )
        ).trim();

        if (reason.isEmpty()) {
            sender.sendMessage(Messages.SUS_REMOVE_USAGE);
            return true;
        }

        lookupPlayer(playerName)
                .thenCompose(result -> {

                    if (!result.found()) {

                        return CompletableFuture.failedFuture(
                                new IllegalArgumentException(
                                        "PLAYER_NOT_FOUND"
                                )
                        );
                    }

                    return manager.removeNote(
                            result.uuid(),
                            reason
                    );
                })
                .whenComplete((result, throwable) -> {

                    Bukkit.getScheduler().runTask(
                            plugin,
                            () -> {

                                if (throwable != null) {

                                    Throwable cause =
                                            getCause(throwable);

                                    if ("PLAYER_NOT_FOUND".equals(
                                            cause.getMessage()
                                    )) {

                                        sender.sendMessage(
                                                Messages.UNKNOWN_PLAYER
                                        );

                                    } else {

                                        plugin.getLogger().warning(
                                                "Failed to remove SUS for "
                                                        + playerName
                                                        + ": "
                                                        + cause.getMessage()
                                        );

                                        sender.sendMessage(
                                                Messages.DATABASE_ERROR
                                        );
                                    }

                                    return;
                                }

                                if (result.status()
                                        == SusManager.RemoveResult.Status.NOT_FOUND) {

                                    sender.sendMessage(
                                            Messages.susReasonNotFound(
                                                    playerName,
                                                    reason
                                            )
                                    );

                                    return;
                                }

                                sender.sendMessage(
                                        Messages.susRemoved(
                                                result.note()
                                        )
                                );
                            }
                    );
                });

        return true;
    }

    private CompletableFuture<PlayerLookup.Result> lookupPlayer(
            String playerName
    ) {

        return PlayerLookup.find(
                plugin,
                playerName
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        return complete(sender, args);
    }

    /**
     * Tab completion for /offence sus ...
     */
    public List<String> tabCompleteFromOffence(
            CommandSender sender,
            String[] args
    ) {

        if (args.length == 0
                || !args[0].equalsIgnoreCase("sus")) {
            return List.of();
        }

        String[] susArgs = Arrays.copyOfRange(
                args,
                1,
                args.length
        );

        return complete(sender, susArgs);
    }

    private List<String> complete(
            CommandSender sender,
            String[] args
    ) {

        /*
         * /sus <TAB>
         */
        if (args.length == 1) {

            String input = args[0]
                    .toLowerCase(Locale.ROOT);

            List<String> suggestions =
                    new ArrayList<>();

            if (hasPermission(
                    sender,
                    "grassgg.staff.sus.add"
            ) && "add".startsWith(input)) {

                suggestions.add("add");
            }

            if (hasPermission(
                    sender,
                    "grassgg.staff.sus.remove"
            ) && "remove".startsWith(input)) {

                suggestions.add("remove");
            }

            return suggestions;
        }

        String subCommand =
                args[0].toLowerCase(Locale.ROOT);

        /*
         * /sus add <PLAYER> <REASON>
         */
        if (subCommand.equals("add")) {

            if (!hasPermission(
                    sender,
                    "grassgg.staff.sus.add"
            )) {
                return List.of();
            }

            if (args.length == 2) {
                return onlinePlayers(args[1]);
            }

            if (args.length >= 3) {

                String reasonInput = String.join(
                        " ",
                        Arrays.copyOfRange(
                                args,
                                2,
                                args.length
                        )
                );

                return configuredReasons(
                        reasonInput
                );
            }
        }

        /*
         * /sus remove <PLAYER> <REASON>
         */
        if (subCommand.equals("remove")) {

            if (!hasPermission(
                    sender,
                    "grassgg.staff.sus.remove"
            )) {
                return List.of();
            }

            if (args.length == 2) {
                return onlineSusPlayers(args[1]);
            }

            if (args.length >= 3) {

                String playerName = args[1];

                String reasonInput = String.join(
                        " ",
                        Arrays.copyOfRange(
                                args,
                                2,
                                args.length
                        )
                );

                return currentReasons(
                        playerName,
                        reasonInput
                );
            }
        }

        return List.of();
    }

    private List<String> onlinePlayers(
            String input
    ) {

        String lower =
                input.toLowerCase(Locale.ROOT);

        return Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .filter(name ->
                        name.toLowerCase(Locale.ROOT)
                                .startsWith(lower)
                )
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private List<String> onlineSusPlayers(
            String input
    ) {

        String lower =
                input.toLowerCase(Locale.ROOT);

        return manager.getPlayers()
                .stream()
                .filter(susPlayer ->
                        Bukkit.getPlayerExact(
                                susPlayer.name()
                        ) != null
                )
                .map(SusPlayer::name)
                .filter(name ->
                        name.toLowerCase(Locale.ROOT)
                                .startsWith(lower)
                )
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private List<String> configuredReasons(
            String input
    ) {

        String lower =
                input.toLowerCase(Locale.ROOT);

        return plugin.getReasonManager()
                .all()
                .stream()
                .map(PunishmentReason::displayName)
                .filter(name ->
                        name.toLowerCase(Locale.ROOT)
                                .startsWith(lower)
                )
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private List<String> currentReasons(
            String playerName,
            String input
    ) {

        /*
         * As with the rest of your staff commands,
         * tab completion only suggests online players.
         */
        Player player =
                Bukkit.getPlayerExact(playerName);

        if (player == null) {
            return List.of();
        }

        String lower =
                input.toLowerCase(Locale.ROOT);

        return manager.getReasons(
                        player.getUniqueId()
                )
                .stream()
                .filter(reason ->
                        reason.toLowerCase(Locale.ROOT)
                                .startsWith(lower)
                )
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private boolean hasPermission(
            CommandSender sender,
            String permission
    ) {

        /*
         * grassgg.staff.sus acts as the parent permission.
         */
        if (sender.hasPermission(
                "grassgg.staff.sus"
        )) {
            return true;
        }

        return sender.hasPermission(permission);
    }

    private Throwable getCause(
            Throwable throwable
    ) {

        Throwable cause = throwable;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause;
    }

    private String getCauseMessage(
            Throwable throwable
    ) {

        Throwable cause =
                getCause(throwable);

        return cause.getMessage() != null
                ? cause.getMessage()
                : cause.getClass().getSimpleName();
    }
}