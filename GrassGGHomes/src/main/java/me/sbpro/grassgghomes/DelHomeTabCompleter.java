package me.sbpro.grassgghomes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DelHomeTabCompleter implements TabCompleter {

    private final GrassGGHomes plugin;

    public DelHomeTabCompleter(GrassGGHomes plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (args.length != 1) {
            return Collections.emptyList();
        }

        List<String> completions =
                new ArrayList<>();

        for (Home home :
                plugin.getHomesManager()
                        .getHomes(player.getUniqueId())
                        .values()) {

            if (home.getName() != null
                    && !home.getName().isBlank()) {

                completions.add(
                        home.getName()
                );
            }
        }

        String input =
                args[0].toLowerCase();

        completions.removeIf(
                name -> !name
                        .toLowerCase()
                        .startsWith(input)
        );

        Collections.sort(completions);

        return completions;
    }
}