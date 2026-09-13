package me.sbpro.grassgghomes;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomesCommand implements CommandExecutor {

    private final GrassGGHomes plugin;

    public HomesCommand(GrassGGHomes plugin) {
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

        /*
         * /homes reload
         */
        if (args.length == 1
                && args[0].equalsIgnoreCase("reload")) {

            if (!player.hasPermission("grassgg.admin")) {

                player.sendMessage(
                        "§2§lHOMES §8»§c No permission."
                );

                return true;
            }

            plugin.getHomesManager().reloadHomes();

            player.sendMessage(
                    "§2§lHOMES §8»§f Homes reloaded."
            );

            return true;
        }


        /*
         * /homes
         *
         * Opens your own Homes GUI.
         */
        if (args.length == 0) {

            new HomesMenu(
                    plugin,
                    player,
                    player
            ).open();

            return true;
        }


        String input =
                String.join(" ", args).trim();


        /*
         * /homes PLAYER:HOME
         *
         * Teleports to another player's home.
         */
        if (input.contains(":")) {

            if (!player.hasPermission("grassgg.homes.staff")) {

                player.sendMessage(
                        "§2§lHOMES §8»§c No permission."
                );

                return true;
            }

            String[] parts =
                    input.split(":", 2);

            String playerName =
                    parts[0].trim();

            String homeName =
                    parts[1].trim();

            if (playerName.isEmpty()
                    || homeName.isEmpty()) {

                player.sendMessage(
                        "§2§lHOMES §8»§c Usage: §f/homes <player>:<home>"
                );

                return true;
            }

            Player target =
                    Bukkit.getPlayerExact(playerName);

            if (target == null) {

                player.sendMessage(
                        "§2§lHOMES §8»§f Player §2"
                                + playerName
                                + " §fis not online."
                );

                return true;
            }

            Home home =
                    plugin.getHomesManager()
                            .getHomeByName(
                                    target.getUniqueId(),
                                    homeName
                            );

            if (home == null) {

                player.sendMessage(
                        "§2§lHOMES §8»§f That player does not have a home named §2"
                                + homeName
                                + "§f."
                );

                return true;
            }

            player.closeInventory();

            player.sendMessage(
                    "§2§lHOMES §8»§f Teleporting to §2"
                            + target.getName()
                            + "'s "
                            + homeName
                            + "§f."
            );

            new TeleportManager(
                    plugin,
                    player,
                    home.getLocation()
            ).start();

            return true;
        }


        /*
         * /homes PLAYER
         *
         * If an online player exists with this name,
         * open their Homes GUI.
         */
        Player target =
                Bukkit.getPlayerExact(input);

        if (target != null) {

            if (!player.hasPermission("grassgg.homes.staff")) {

                player.sendMessage(
                        "§2§lHOMES §8»§c No permission."
                );

                return true;
            }

            new HomesMenu(
                    plugin,
                    player,
                    target
            ).open();

            return true;
        }


        /*
         * /homes HOME
         *
         * Otherwise treat the argument as one
         * of your own home names.
         */
        Home home =
                plugin.getHomesManager()
                        .getHomeByName(
                                player.getUniqueId(),
                                input
                        );

        if (home == null) {

            player.sendMessage(
                    "§2§lHOMES §8»§f Home §2"
                            + input
                            + " §fdoes not exist."
            );

            return true;
        }

        player.closeInventory();

        player.sendMessage(
                "§2§lHOMES §8»§f Teleporting to §2"
                        + input
                        + "§f."
        );

        new TeleportManager(
                plugin,
                player,
                home.getLocation()
        ).start();

        return true;
    }
}