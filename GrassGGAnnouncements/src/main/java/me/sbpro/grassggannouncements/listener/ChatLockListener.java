package me.sbpro.grassggannouncements.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.sbpro.grassggannouncements.command.ChatLockManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatLockListener implements Listener {

    private final ChatLockManager chatLockManager;

    public ChatLockListener(ChatLockManager chatLockManager) {
        this.chatLockManager = chatLockManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {

        if (!chatLockManager.isChatLocked()) {
            return;
        }

        event.setCancelled(true);

        event.getPlayer().sendMessage(
                Component.text("Chat is currently locked because of a new announcement.")
                        .color(TextColor.color(0xFF5555))
        );
    }
}