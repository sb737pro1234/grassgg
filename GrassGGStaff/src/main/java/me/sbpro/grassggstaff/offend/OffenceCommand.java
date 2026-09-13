package me.sbpro.grassggstaff.offend;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.sus.SusCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class OffenceCommand
        implements CommandExecutor, TabCompleter {

    private final GrassGGStaff plugin;
    private final SusCommand susCommand;

    public OffenceCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
        this.susCommand = new SusCommand(plugin);
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        /*
         * SUS has separate permissions, so handle it
         * before checking grassgg.staff.
         */
        if (args.length > 0
                && args[0].equalsIgnoreCase("sus")) {

            return susCommand.executeFromOffence(
                    sender,
                    args
            );
        }

        /*
         * All normal offence commands require
         * grassgg.staff.
         */
        if (!sender.hasPermission("grassgg.staff")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Messages.OFFENCE_USAGE);
            return true;
        }

        return switch (
                args[0].toLowerCase(Locale.ROOT)
                ) {

            case "new" ->
                    new OffenceNewCommand(plugin)
                            .execute(sender, args);

            case "check" ->
                    new OffenceCheckCommand(plugin)
                            .execute(sender, args);

            case "list" ->
                    new OffenceListCommand(plugin)
                            .execute(sender, args);

            case "change" ->
                    new OffenceChangeCommand(plugin)
                            .execute(sender, args);

            case "remove" ->
                    new OffenceRemoveCommand(plugin)
                            .execute(sender, args);

            case "reset" ->
                    new OffenceResetCommand(plugin)
                            .execute(sender, args);

            default -> {
                sender.sendMessage(Messages.OFFENCE_USAGE);
                yield true;
            }
        };
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        /*
         * ============================================================
         *                    /offence sus ...
         * ============================================================
         */

        if (args.length > 0
                && args[0].equalsIgnoreCase("sus")) {

            return susCommand.tabCompleteFromOffence(
                    sender,
                    args
            );
        }

        /*
         * ============================================================
         *                  /offence <TAB>
         * ============================================================
         */

        if (args.length == 1) {

            String input =
                    args[0].toLowerCase(Locale.ROOT);

            List<String> suggestions =
                    new ArrayList<>();

            /*
             * Normal offence commands.
             */
            if (sender.hasPermission("grassgg.staff")) {

                addIfMatches(
                        suggestions,
                        "new",
                        input
                );

                addIfMatches(
                        suggestions,
                        "check",
                        input
                );

                addIfMatches(
                        suggestions,
                        "list",
                        input
                );

                addIfMatches(
                        suggestions,
                        "change",
                        input
                );

                addIfMatches(
                        suggestions,
                        "remove",
                        input
                );

                addIfMatches(
                        suggestions,
                        "reset",
                        input
                );
            }

            /*
             * SUS has its own permissions.
             *
             * This allows:
             * /offence s<TAB>
             *
             * even when the player doesn't have
             * grassgg.staff.
             */
            if (hasSusPermission(sender)) {

                addIfMatches(
                        suggestions,
                        "sus",
                        input
                );
            }

            return suggestions;
        }

        /*
         * ============================================================
         *                NORMAL SUBCOMMAND COMPLETION
         * ============================================================
         */

        if (!sender.hasPermission("grassgg.staff")) {
            return List.of();
        }

        String subCommand =
                args[0].toLowerCase(Locale.ROOT);

        return switch (subCommand) {

            case "new" ->
                    new OffenceNewCommand(plugin)
                            .tabComplete(sender, args);

            case "check" ->
                    new OffenceCheckCommand(plugin)
                            .tabComplete(sender, args);

            case "list" ->
                    new OffenceListCommand(plugin)
                            .tabComplete(sender, args);

            case "change" ->
                    new OffenceChangeCommand(plugin)
                            .tabComplete(sender, args);

            case "remove" ->
                    new OffenceRemoveCommand(plugin)
                            .tabComplete(sender, args);

            case "reset" ->
                    new OffenceResetCommand(plugin)
                            .tabComplete(sender, args);

            default ->
                    List.of();
        };
    }

    /**
     * Adds a suggestion when the user's current input
     * starts with the supplied command.
     */
    private void addIfMatches(
            List<String> suggestions,
            String value,
            String input
    ) {

        if (value.startsWith(input)) {
            suggestions.add(value);
        }
    }

    /**
     * Checks whether the sender has any SUS permission.
     *
     * This is deliberately separate from grassgg.staff.
     */
    private boolean hasSusPermission(
            CommandSender sender
    ) {

        return sender.hasPermission(
                "grassgg.staff.sus"
        )
                || sender.hasPermission(
                "grassgg.staff.sus.add"
        )
                || sender.hasPermission(
                "grassgg.staff.sus.remove"
        );
    }
}