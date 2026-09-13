package me.sbpro.grassggannouncements;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnouncementsCommand implements CommandExecutor {

    private final GrassGGAnnouncements plugin;

    public AnnouncementsCommand(GrassGGAnnouncements plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("grassggannouncements.admin")) {
            sender.sendMessage(
                    "§cYou do not have permission to use this command."
            );
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            plugin.getAnnouncementManager().reload();

            sender.sendMessage(
                    "§aGrassGGAnnouncements configuration reloaded!"
            );

            return true;
        }

        sender.sendMessage(
                "§cUsage: /announcements reload"
        );

        return true;
    }
}