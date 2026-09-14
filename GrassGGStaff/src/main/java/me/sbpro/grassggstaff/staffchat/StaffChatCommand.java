package me.sbpro.grassggstaff.staffchat;

import me.sbpro.grassggstaff.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public final class StaffChatCommand
        implements CommandExecutor, TabCompleter {

    private final StaffChatManager manager;

    public StaffChatCommand(
            StaffChatManager manager
    ) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    Messages.PREFIX
                            + "§cOnly players can use staff chat."
            );

            return true;
        }

        if (!player.hasPermission(
                "grassgg.staff.chat"
        )) {

            player.sendMessage(
                    Messages.NO_PERMISSION
            );

            return true;
        }

        manager.toggle(player);

        if (manager.isEnabled(player)) {

            player.sendMessage(
                    Messages.PREFIX
                            + "§fStaff chat has been §aenabled§f."
            );

        } else {

            player.sendMessage(
                    Messages.PREFIX
                            + "§fStaff chat has been §cdisabled§f."
            );
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        return List.of();
    }
}