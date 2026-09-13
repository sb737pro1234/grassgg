package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.permission.Permissions;
import me.sbpro.grassggteleport.request.TeleportRequestManager;
import me.sbpro.grassggteleport.request.TeleportRequestType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpahereCommand implements CommandExecutor {

    private final TeleportRequestManager requestManager;

    public TpahereCommand(GrassGGTeleport plugin) {
        this.requestManager = plugin.getTeleportRequestManager();
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

        if (!player.hasPermission(Permissions.TPAHERE)) {
            Messages.sendError(
                    player,
                    "You do not have permission to use /tpahere."
            );
            return true;
        }

        if (args.length != 1) {
            Messages.sendError(
                    player,
                    "Usage: /tpahere <player>"
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
                    "You cannot send a teleport request to yourself."
            );
            return true;
        }

        requestManager.sendRequest(
                player,
                target,
                TeleportRequestType.TPAHERE
        );

        return true;
    }
}