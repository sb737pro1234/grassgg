package me.sbpro.grassgg.commands.say;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("grassgg.say")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /say <text>");
            return true;
        }

        String message = String.join(" ", args);

        String titleLine = "§x§2§9§7§9§F§F"+ player.getName() + ChatColor.WHITE + ":";
        String subtitleLine = ChatColor.WHITE + message;

        for (Player online : player.getServer().getOnlinePlayers()) {
            online.sendTitle(titleLine, subtitleLine, 10, 70, 20);
        }

        String confirmation = "§x§2§9§7§9§F§F§lSTAFF §8» §fYou sent the message: §x§2§9§7§9§F§F\""
                + message + "\" §fto all online players.";
        player.sendMessage(confirmation);

        return true;
    }
}