package me.sbpro.grassggteleport.command.tab;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PlayerOnlyTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length != 1) {
            return List.of();
        }

        String input = args[0].toLowerCase();

        List<String> completions = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(input)) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}