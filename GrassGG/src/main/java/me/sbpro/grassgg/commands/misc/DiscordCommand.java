package me.sbpro.grassgg.commands.misc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§2§lGRASS.GG §8» §4Only players can use this command.");
            return true;
        }

        player.sendMessage("§2§lGRASS.GG §8» §fJoin the discord: §2discord.gg/A97Dkcb7TU");
        return true;
    }


}