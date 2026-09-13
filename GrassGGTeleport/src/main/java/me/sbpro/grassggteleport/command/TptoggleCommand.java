package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.permission.Permissions;
import me.sbpro.grassggteleport.settings.TeleportPreferencesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TptoggleCommand implements CommandExecutor {

    private final TeleportPreferencesManager preferencesManager;

    public TptoggleCommand(GrassGGTeleport plugin) {
        this.preferencesManager =
                plugin.getTeleportPreferencesManager();
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

        if (!player.hasPermission(Permissions.TPTOGGLE)) {
            Messages.sendError(
                    player,
                    "You do not have permission to use /tptoggle."
            );
            return true;
        }

        if (args.length != 0) {
            Messages.sendError(
                    player,
                    "Usage: /tptoggle"
            );
            return true;
        }

        boolean disabled =
                preferencesManager.toggleTeleportRequests(player);

        if (disabled) {
            player.sendMessage(
                    Messages.tptoggleEnabled()
            );
        } else {
            player.sendMessage(
                    Messages.tptoggleDisabled()
            );
        }

        return true;
    }
}