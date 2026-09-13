package me.sbpro.grassggwarp.commands.crates;

import me.sbpro.grassggwarp.WarpHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCratesCommand implements CommandExecutor {

    private final WarpHelper warpHelper;

    public SetCratesCommand(WarpHelper warpHelper) {
        this.warpHelper = warpHelper;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    "§x§0§2§D§8§E§9§lWARP §8»§c You must be a player to use this command."
            );
            return true;
        }

        warpHelper.setWarp("crates", player.getLocation());

        player.sendMessage(
                "§x§0§2§D§8§E§9§lWARP §8»§a Crates location set!"
        );

        return true;
    }
}