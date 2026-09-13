package me.sbpro.grassgg.commands.misc;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /ping
        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage("§2§lGRASS.GG §8»§c You must specify a player from console.");
                return true;
            }

            player.sendMessage("§2§lGRASS.GG §8»§2 Your ping is §a" + player.getPing() + "ms§2.");
            return true;
        }

        // /ping <player>
        if (args.length == 1) {

            if (sender instanceof Player player && !player.hasPermission("grassgg.ping.others")) {
                player.sendMessage("§2§lGRASS.GG §8»§c You do not have permission to check other players ping.");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                sender.sendMessage("§2§lGRASS.GG §8»§c That player is not online.");
                return true;
            }

            sender.sendMessage("§2§lGRASS.GG §8»§a " + target.getName() + "'§2s ping is §a" + target.getPing() + "ms§2."
            );

            return true;
        }

        sender.sendMessage("§2§lGRASS.GG §8»§c Usage: /ping [player]");
        return true;
    }
}