package me.sbpro.grassggteleport.command;

import me.sbpro.grassggteleport.GrassGGTeleport;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpallCommand implements CommandExecutor {

    private final TeleportCommand teleportCommand;

    public TpallCommand(GrassGGTeleport plugin) {
        this.teleportCommand = new TeleportCommand(plugin);
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

        return teleportCommand.executeTpall(player);
    }
}