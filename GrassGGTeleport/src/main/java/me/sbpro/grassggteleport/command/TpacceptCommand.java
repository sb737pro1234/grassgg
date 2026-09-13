package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.permission.Permissions;
import me.sbpro.grassggteleport.request.TeleportRequestManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpacceptCommand implements CommandExecutor {

    private final TeleportRequestManager requestManager;

    public TpacceptCommand(GrassGGTeleport plugin) {
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

        if (!player.hasPermission(Permissions.TPACCEPT)) {
            Messages.sendError(
                    player,
                    "You do not have permission to use /tpaccept."
            );
            return true;
        }

        if (args.length == 0) {
            requestManager.acceptMostRecent(player);
            return true;
        }

        if (args.length == 1) {
            Player requester = Bukkit.getPlayerExact(args[0]);

            if (requester == null) {
                Messages.sendError(
                        player,
                        "Player not found."
                );
                return true;
            }

            requestManager.acceptFrom(
                    player,
                    requester
            );

            return true;
        }

        Messages.sendError(
                player,
                "Usage: /tpaccept [player]"
        );

        return true;
    }
}