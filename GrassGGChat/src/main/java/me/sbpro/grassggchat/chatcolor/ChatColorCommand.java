package me.sbpro.grassggchat.chatcolor;

import me.sbpro.grassggchat.GrassGGChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatColorCommand implements CommandExecutor {

    private final ChatColorManager manager;

    public ChatColorCommand(
            ChatColorManager manager
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
                    GrassGGChat.PREFIX +
                            "Only players can use this command."
            );

            return true;
        }

        if (!player.hasPermission(
                "grassgg.chatcolor.command"
        )) {

            player.sendMessage(
                    GrassGGChat.PREFIX +
                            "You don't have permission to use this command."
            );

            return true;
        }

        ChatColorMenu.open(
                player,
                manager
        );

        return true;
    }
}