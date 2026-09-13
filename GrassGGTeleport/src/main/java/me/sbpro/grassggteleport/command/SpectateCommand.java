package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import me.sbpro.grassggteleport.gui.ConfirmationMenu;
import me.sbpro.grassggteleport.message.Messages;
import me.sbpro.grassggteleport.permission.Permissions;
import me.sbpro.grassggteleport.spectate.SpectateAction;
import me.sbpro.grassggteleport.spectate.SpectateManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SpectateCommand implements CommandExecutor {

    private final SpectateManager spectateManager;

    public SpectateCommand(GrassGGTeleport plugin) {
        this.spectateManager =
                plugin.getSpectateManager();
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

        if (!player.hasPermission(Permissions.SPECTATE)) {
            Messages.sendError(
                    player,
                    "You do not have permission to use /spectate."
            );
            return true;
        }

        if (args.length == 0) {
            if (!spectateManager.stopSpectating(player)) {
                Messages.sendError(
                        player,
                        "You are not currently spectating a player."
                );
            }

            return true;
        }

        if (args.length != 1) {
            Messages.sendError(
                    player,
                    "Usage: /spectate [player]"
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
                    "You cannot spectate yourself."
            );
            return true;
        }

        ConfirmationMenu.open(
                player,
                "spectate " + target.getName(),
                new SpectateAction(
                        spectateManager,
                        player,
                        target
                )
        );

        return true;
    }
}