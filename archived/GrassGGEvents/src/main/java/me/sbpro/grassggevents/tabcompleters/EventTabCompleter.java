package me.sbpro.grassggevents.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class EventTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {

            if ("sumo".startsWith(args[0].toLowerCase())) {
                completions.add("sumo");
            }

        } else if (args.length == 2 && args[0].equalsIgnoreCase("sumo")) {

            String input = args[1].toLowerCase();

            for (String option : new String[]{"pos1", "pos2", "save", "start", "end"}) {
                if (option.startsWith(input)) {
                    completions.add(option);
                }
            }
        }

        return completions;
    }
}