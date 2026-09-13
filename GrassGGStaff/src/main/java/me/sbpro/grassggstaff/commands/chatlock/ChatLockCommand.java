package me.sbpro.grassggstaff.commands.chatlock;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatLockCommand implements CommandExecutor {

    public static boolean chatLocked = false;

    public static final String PREFIX = "§x§2§9§7§9§F§F§lSTAFF §8» §f";

    private static final String LOCKED_MESSAGE =
            PREFIX + ChatColor.RED + "Chat is now locked.";

    private static final String UNLOCKED_MESSAGE =
            PREFIX + ChatColor.GREEN + "Chat has been unlocked. You may type as normal.";

    private static final String NO_PERMISSION =
            PREFIX + ChatColor.RED + "You do not have permission to use this command.";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("grassgg.chatlock")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        chatLocked = !chatLocked;

        if (chatLocked) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(LOCKED_MESSAGE);}
            Bukkit.getConsoleSender().sendMessage(LOCKED_MESSAGE);
        } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(UNLOCKED_MESSAGE);}
            Bukkit.getConsoleSender().sendMessage(UNLOCKED_MESSAGE);
        }

        return true;
    }
}