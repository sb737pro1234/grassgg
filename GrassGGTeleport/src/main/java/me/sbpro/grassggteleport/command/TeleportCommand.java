package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.permission.Permissions;
import me.sbpro.grassggteleport.teleport.TeleportManager;
import me.sbpro.grassggteleport.util.CoordinateParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class TeleportCommand implements CommandExecutor {

    private final TeleportManager teleportManager;

    public TeleportCommand(GrassGGTeleport plugin) {
        this.teleportManager = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if (!player.hasPermission(Permissions.TP)) {
            Messages.sendError(player, "You do not have permission to use /tp.");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("@a")) {
                return teleportAll(player);
            }

            return teleportSelfToPlayer(player, args[0]);
        }

        if (args.length == 2) {
            return teleportPlayerToPlayer(
                    player,
                    args[0],
                    args[1]
            );
        }

        if (args.length == 3) {
            return teleportCoordinates(
                    player,
                    args[0],
                    args[1],
                    args[2]
            );
        }

        if (args.length == 4) {
            return teleportToCoordinates(
                    player,
                    args[0],
                    args[1],
                    args[2],
                    args[3]
            );
        }

        Messages.sendError(
                player,
                "Usage: /tp <player> [player] or /tp <x> <y> <z>"
        );

        return true;
    }

    private boolean teleportSelfToPlayer(
            Player sender,
            String targetInput
    ) {
        Player target = resolveSinglePlayer(sender, targetInput);

        if (target == null) {
            return true;
        }

        if (target.equals(sender)) {
            Messages.sendError(
                    sender,
                    "You cannot teleport to yourself."
            );
            return true;
        }

        teleportManager.requestAdminTeleport(
                sender,
                sender,
                target.getLocation(),
                "teleport you to " + target.getName()
        );

        return true;
    }

    private boolean teleportPlayerToPlayer(
            Player sender,
            String playerInput,
            String targetInput
    ) {
        if (!sender.hasPermission(Permissions.TP_OTHERS)) {
            Messages.sendError(
                    sender,
                    "You do not have permission to teleport other players."
            );
            return true;
        }

        Player player = resolveSinglePlayer(sender, playerInput);

        if (player == null) {
            return true;
        }

        Player target = resolveSinglePlayer(sender, targetInput);

        if (target == null) {
            return true;
        }

        if (player.equals(target)) {
            Messages.sendError(
                    sender,
                    "You cannot teleport a player to themselves."
            );
            return true;
        }

        teleportManager.requestAdminTeleport(
                sender,
                player,
                target.getLocation(),
                "teleport " + player.getName()
                        + " to " + target.getName()
        );

        return true;
    }

    private boolean teleportCoordinates(
            Player sender,
            String xInput,
            String yInput,
            String zInput
    ) {
        if (!sender.hasPermission(Permissions.TP_COORDINATES)) {
            Messages.sendError(
                    sender,
                    "You do not have permission to use coordinate teleportation."
            );
            return true;
        }

        Location destination;

        try {
            destination = CoordinateParser.parse(
                    sender.getLocation(),
                    xInput,
                    yInput,
                    zInput
            );
        } catch (IllegalArgumentException exception) {
            Messages.sendError(
                    sender,
                    "Invalid coordinates."
            );
            return true;
        }

        String formattedLocation = formatCoordinates(destination);

        teleportManager.requestAdminTeleport(
                sender,
                sender,
                destination,
                "teleport you to " + formattedLocation
        );

        return true;
    }

    private boolean teleportToCoordinates(
            Player sender,
            String targetInput,
            String xInput,
            String yInput,
            String zInput
    ) {
        if (!sender.hasPermission(Permissions.TP_COORDINATES)) {
            Messages.sendError(
                    sender,
                    "You do not have permission to use coordinate teleportation."
            );
            return true;
        }

        Player target = resolveSinglePlayer(sender, targetInput);

        if (target == null) {
            return true;
        }

        Location destination;

        try {
            destination = CoordinateParser.parse(
                    sender.getLocation(),
                    xInput,
                    yInput,
                    zInput
            );
        } catch (IllegalArgumentException exception) {
            Messages.sendError(
                    sender,
                    "Invalid coordinates."
            );
            return true;
        }

        String formattedLocation = formatCoordinates(
                destination
        );

        teleportManager.requestAdminTeleport(
                sender,
                target,
                destination,
                "teleport "
                        + target.getName()
                        + " to "
                        + formattedLocation
        );

        return true;
    }

    public boolean executeTpall(Player sender) {
        return teleportAll(sender);
    }
    private boolean teleportAll(Player sender) {
        if (!sender.hasPermission(Permissions.TPALL)) {
            Messages.sendError(
                    sender,
                    "You do not have permission to use /tpall."
            );
            return true;
        }

        List<Player> players = new ArrayList<>(
                Bukkit.getOnlinePlayers().stream()
                        .filter(player -> !player.equals(sender))
                        .filter(player -> !player.hasPermission(
                                Permissions.TPALL_BYPASS
                        ))
                        .toList()
        );

        if (players.isEmpty()) {
            Messages.sendError(
                    sender,
                    "There are no players to teleport."
            );
            return true;
        }

        teleportManager.requestAdminTeleportMultiple(
                sender,
                players,
                sender.getLocation(),
                "teleport all players to you"
        );

        return true;
    }

    private Player resolveSinglePlayer(
            CommandSender sender,
            String input
    ) {
        Player exactPlayer = Bukkit.getPlayerExact(input);

        if (exactPlayer != null) {
            return exactPlayer;
        }

        if (!input.startsWith("@")) {
            Messages.sendError(
                    (Player) sender,
                    "Player not found."
            );
            return null;
        }

        try {
            List<Entity> entities = Bukkit.selectEntities(
                    sender,
                    input
            );

            List<Player> players = entities.stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .toList();

            if (players.size() != 1) {
                Messages.sendError(
                        (Player) sender,
                        "That selector must select exactly one player."
                );
                return null;
            }

            return players.getFirst();

        } catch (IllegalArgumentException exception) {
            Messages.sendError(
                    (Player) sender,
                    "Invalid selector."
            );
            return null;
        }
    }

    private String formatCoordinates(Location location) {
        return formatNumber(location.getX())
                + ", "
                + formatNumber(location.getY())
                + ", "
                + formatNumber(location.getZ());
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }

        return Double.toString(value);
    }
}