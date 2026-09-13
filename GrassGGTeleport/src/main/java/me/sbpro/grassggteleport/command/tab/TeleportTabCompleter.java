package me.sbpro.grassggteleport.command.tab;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class TeleportTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();

            List<String> completions = new ArrayList<>();

            if ("@".startsWith(input)) {
                completions.add("@s");
                completions.add("@p");
                completions.add("@a");
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }

            if ("~".startsWith(input)) {
                completions.add("~");
            }

            return completions;
        }

        if (args.length >= 2 && args.length <= 4) {
            String input = args[args.length - 1];

            if (input.isEmpty() || input.startsWith("~") || isNumberStart(input)) {
                return List.of("~");
            }

            if (args.length == 2) {
                List<String> completions = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName()
                            .toLowerCase()
                            .startsWith(input.toLowerCase())) {
                        completions.add(player.getName());
                    }
                }

                return completions;
            }
        }

        return List.of();
    }

    private boolean isNumberStart(String input) {
        if (input.isEmpty()) {
            return false;
        }

        char first = input.charAt(0);

        return first == '-'
                || first == '+'
                || Character.isDigit(first);
    }
}