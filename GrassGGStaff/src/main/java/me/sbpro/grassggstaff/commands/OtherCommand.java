package me.sbpro.grassggstaff.commands;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class OtherCommand implements CommandExecutor {

    private final GrassGGStaff plugin;

    public OtherCommand(GrassGGStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if(!sender.hasPermission("grassgg.staff")){
            sender.sendMessage(Messages.NO_PERMISSION);
        } else {
            sender.sendMessage(Messages.PREFIX + "This command is not available. Please use /offend");
        }

        return true;
    }
}