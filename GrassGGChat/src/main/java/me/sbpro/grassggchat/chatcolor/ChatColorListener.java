package me.sbpro.grassggchat.chatcolor;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatColorListener implements Listener {

    private final ChatColorManager manager;

    public ChatColorListener(
            ChatColorManager manager
    ) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(
            AsyncChatEvent event
    ) {

        Player player =
                event.getPlayer();

        PlayerChatColor color =
                manager.getColor(
                        player.getUniqueId()
                );

        boolean bold =
                manager.isBold(
                        player.getUniqueId()
                );

        event.message(
                event.message()
                        .color(
                                color.getColor()
                        )
                        .decoration(
                                TextDecoration.BOLD,
                                bold
                        )
        );
    }
}