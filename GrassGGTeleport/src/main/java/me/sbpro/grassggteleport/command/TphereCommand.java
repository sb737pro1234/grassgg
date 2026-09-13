package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.permission.Permissions;
import me.sbpro.grassggteleport.teleport.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TphereCommand implements CommandExecutor {

    private final TeleportManager teleportManager;

    public TphereCommand(GrassGGTeleport plugin) {
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
            sender.sendMessage(
                    "This command can only be used by a player."
            );
            return true;
        }

        if (!player.hasPermission(Permissions.TPHERE)) {
            Messages.sendError(
                    player,
                    "You do not have permission to use /tphere."
            );
            return true;
        }

        if (args.length != 1) {
            Messages.sendError(
                    player,
                    "Usage: /tphere <player>"
            );
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            Messages.sendError(
                    player,
                    "Player not found."
            );
            return true;
        }

        if (target.equals(player)) {
            Messages.sendError(
                    player,
                    "You cannot teleport a player to themselves."
            );
            return true;
        }

        teleportManager.requestAdminTeleport(
                player,
                target,
                player.getLocation(),
                "teleport " + target.getName() + " to you"
        );

        return true;
    }
}