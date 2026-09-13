package me.sbpro.grassggstatistics.stats;

import me.sbpro.grassggstatistics.GrassGGStatistics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatisticsCommand implements CommandExecutor {

    private final GrassGGStatistics plugin;

    public StatisticsCommand(GrassGGStatistics plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§2§lGRASS.GG §8» §4Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("grassgg.statistics")) {
            player.sendMessage("§4You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            StatisticsMenu.open(player, player);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage("§2§lGRASS.GG §8» §4That player is not online.");
            return true;
        }

        StatisticsMenu.open(player, target);

        return true;
    }
}