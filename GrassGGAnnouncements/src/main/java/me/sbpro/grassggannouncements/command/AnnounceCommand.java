package me.sbpro.grassggannouncements.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AnnounceCommand implements CommandExecutor {

    private final ChatLockManager chatLockManager;

    public AnnounceCommand(ChatLockManager chatLockManager) {
        this.chatLockManager = chatLockManager;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("grassgg.announce")) {
            player.sendMessage(Component.text("You do not have permission to use this command.")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /announce [announcement]")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        String announcement = String.join(" ", args);

        // Title
        Component title = Component.text("NEW ANNOUNCEMENT")
                .color(TextColor.color(0xFFFF00))
                .decorate(TextDecoration.BOLD);

        Component subtitle = Component.text("ᴘʟᴇᴀѕᴇ ʀᴇᴀᴅ ᴛʜᴇ ᴍᴇѕѕᴀɢᴇ ɪɴ ᴄʜᴀᴛ")
                .color(TextColor.color(0xFFFFFF));

        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.showTitle(
                        net.kyori.adventure.title.Title.title(
                                title,
                                subtitle
                        )
                )
        );

        // Chat announcement
        Component message = Component.text()
                .append(
                        Component.text("ᴄʜᴀᴛ ɪѕ ᴘᴀᴜѕᴇᴅ ꜰᴏʀ 5 ѕᴇᴄᴏɴᴅѕ.")
                                .color(TextColor.color(0xFF5555))
                )
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .append(
                        Component.text("NEW ANNOUNCEMENT:")
                                .decorate(TextDecoration.BOLD)
                                .color(TextColor.color(0xFFFF00))
                                .decorate(TextDecoration.UNDERLINED)
                )
                .append(
                        Component.text(" (From " + player.getName() + ")")
                                .color(TextColor.color(0xFFFFFF))
                )
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .append(
                        Component.text(announcement)
                                .color(TextColor.color(0xFFFF00))
                )
                .append(Component.text("\n"))
                .append(Component.text("\n"))



                .build();

        Bukkit.broadcast(message);

        // Lock chat for 5 seconds
        chatLockManager.lockChat();

        return true;
    }
}