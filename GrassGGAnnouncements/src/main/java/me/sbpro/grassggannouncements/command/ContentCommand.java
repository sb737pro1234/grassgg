package me.sbpro.grassggannouncements.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public class ContentCommand implements CommandExecutor {

    private final ChatLockManager chatLockManager;

    public ContentCommand(ChatLockManager chatLockManager) {
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

        if (!player.hasPermission("grassgg.media.announce")) {
            player.sendMessage(Component.text("You do not have permission to use this command.")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /content [url]")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        String url = normalizeUrl(args[0]);

        if (url == null) {
            player.sendMessage(Component.text("That doesn't look like a valid URL.")
                    .color(TextColor.color(0xFF5555)));
            return true;
        }

        String displayUrl = stripScheme(url);

        TextColor pink = TextColor.color(0xFF69B4);

        // Title
        Component title = Component.text(player.getName().toUpperCase() + " HAS UPLOADED")
                .color(pink)
                .decorate(TextDecoration.BOLD);

        Component subtitle = Component.text(displayUrl)
                .color(TextColor.color(0xFFFFFF));

        Bukkit.getOnlinePlayers().forEach(onlinePlayer ->
                onlinePlayer.showTitle(
                        net.kyori.adventure.title.Title.title(
                                title,
                                subtitle
                        )
                )
        );

        // Clickable link component (displays without scheme, opens with full URL)
        Component link = Component.text(displayUrl)
                .color(TextColor.color(0xFFFFFF))
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open content")));

        // Chat announcement
        Component message = Component.text()
                .append(
                        Component.text("ᴄʜᴀᴛ ɪѕ ᴘᴀᴜѕᴇᴅ ꜰᴏʀ 5 ѕᴇᴄᴏɴᴅѕ.")
                                .color(TextColor.color(0xFF5555))
                )
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .append(
                        Component.text("NEW CONTENT:")
                                .decorate(TextDecoration.BOLD)
                                .color(pink)
                                .decorate(TextDecoration.UNDERLINED)
                )
                .append(
                        Component.text(" (From " + player.getName() + ")")
                                .color(TextColor.color(0xFFFFFF))
                )
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .append(link)
                .append(Component.text("\n"))
                .append(Component.text("\n"))
                .build();

        Bukkit.broadcast(message);

        // Lock chat for 5 seconds
        chatLockManager.lockChat();

        return true;
    }

    /**
     * Normalizes user input into a full URL, adding "https://" if no scheme
     * is present (e.g. "www.twitch.tv/foo" or "twitch.tv/foo").
     * Returns null if the result is not a valid http/https URL.
     */
    private String normalizeUrl(String input) {
        String candidate = input;

        if (!candidate.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*")) {
            candidate = "https://" + candidate;
        }

        try {
            URL parsed = new URL(candidate);
            String protocol = parsed.getProtocol();
            String host = parsed.getHost();

            if (!("http".equals(protocol) || "https".equals(protocol))) {
                return null;
            }

            if (host == null || host.isEmpty() || !host.contains(".")) {
                return null;
            }

            return candidate;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Strips a leading "https://" or "http://" for display purposes only.
     * The full URL (with scheme) should still be used for the actual click event.
     */
    private String stripScheme(String url) {
        if (url.startsWith("https://")) {
            return url.substring("https://".length());
        }
        if (url.startsWith("http://")) {
            return url.substring("http://".length());
        }
        return url;
    }
}