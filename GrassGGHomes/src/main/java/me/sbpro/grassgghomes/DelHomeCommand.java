package me.sbpro.grassgghomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {

    private final GrassGGHomes plugin;

    public DelHomeCommand(GrassGGHomes plugin) {
        this.plugin = plugin;
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
                    "§cOnly players can use this command."
            );

            return true;
        }

        if (args.length == 0) {

            player.sendMessage(
                    "§2§lHOMES §8»§c Usage: /delhome <name>"
            );

            return true;
        }

        String homeName =
                String.join(" ", args).trim();

        int homeNumber =
                plugin.getHomesManager()
                        .getHomeNumber(
                                player.getUniqueId(),
                                homeName
                        );

        if (homeNumber == -1) {

            player.sendMessage(
                    "§2§lHOMES §8»§f Home §2"
                            + homeName
                            + " §fdoes not exist."
            );

            return true;
        }

        /*
         * Open the exact same confirmation menu
         * used by the Homes GUI.
         */
        new DeleteHomeMenu(
                plugin,
                player,
                homeNumber
        ).open();

        return true;
    }
}