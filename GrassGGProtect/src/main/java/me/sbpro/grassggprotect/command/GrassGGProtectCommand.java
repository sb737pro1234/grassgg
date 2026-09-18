package me.sbpro.grassggprotect.command;

import me.sbpro.grassggprotect.GrassGGProtect;
import me.sbpro.grassggprotect.Messages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class GrassGGProtectCommand implements CommandExecutor, TabCompleter {

    private final GrassGGProtect plugin;

    public GrassGGProtectCommand(GrassGGProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("grassgg.admin")) {
            sender.sendMessage(Messages.NO_PERMISSION);
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            plugin.reloadPlugin();

            sender.sendMessage(Messages.RELOADED);
            return true;
        }

        sender.sendMessage(Messages.INVALID_ARGUMENT);
        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {
            return Collections.singletonList("reload");
        }

        return Collections.emptyList();
    }
}