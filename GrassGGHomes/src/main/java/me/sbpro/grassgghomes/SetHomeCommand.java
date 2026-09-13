package me.sbpro.grassgghomes;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetHomeCommand implements CommandExecutor {

    private final GrassGGHomes plugin;

    public SetHomeCommand(GrassGGHomes plugin) {
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
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {

            player.sendMessage(
                    "§2§lHOMES §8»§c Usage: /sethome <name>"
            );

            return true;
        }

        String name = String.join(" ", args).trim();

        if (name.isEmpty()) {

            player.sendMessage(
                    "§2§lHOMES §8»§f Please provide a home name."
            );

            return true;
        }

        if (name.length() > 24) {

            player.sendMessage(
                    "§2§lHOMES §8»§f Home names cannot be longer than §224 §fcharacters."
            );

            return true;
        }

        if (plugin.getHomesManager().getHomeByName(
                player.getUniqueId(),
                name
        ) != null) {

            player.sendMessage(
                    "§2§lHOMES §8»§f You already have a home named §2"
                            + name
                            + "§f."
            );

            return true;
        }

        int maxHomes = getHomeLimit(player);

        if (maxHomes == 0) {

            player.sendMessage(
                    "§2§lHOMES §8»§c No permission."
            );

            return true;
        }

        int nextHome =
                plugin.getHomesManager().getNextAvailableHome(
                        player.getUniqueId(),
                        maxHomes
                );

        if (nextHome == -1) {

            player.sendMessage(
                    "§2§lHOMES §8»§f You have no available home slots."
            );

            return true;
        }

        Location location = player.getLocation();

        if (!isAllowedWorld(location.getWorld())) {

            player.sendMessage(
                    "§2§lHOMES §8»§f You cannot set a home here."
            );

            return true;
        }

        plugin.getHomesManager().setHome(
                player.getUniqueId(),
                nextHome,
                location
        );

        Home home =
                plugin.getHomesManager().getHome(
                        player.getUniqueId(),
                        nextHome
                );

        if (home != null) {
            home.setName(name);
            plugin.getHomesManager().saveHomes();
        }

        player.sendMessage(
                "§2§lHOMES §8»§f Home §2"
                        + name
                        + " §fcreated."
        );

        return true;
    }

    private int getHomeLimit(Player player) {

        for (int i = 7; i >= 1; i--) {

            if (player.hasPermission(
                    "grassgg.homes." + i
            )) {

                return i;
            }
        }

        return 0;
    }

    private boolean isAllowedWorld(World world) {

        return world != null
                && List.of(
                "world",
                "world_nether",
                "world_the_end"
        ).contains(world.getName());
    }
}