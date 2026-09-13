package me.sbpro.grassggstaff.commands.chatlock;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatLockListener implements Listener {

    public static final String PREFIX = "§x§2§9§7§9§F§F§lSTAFF §8» §f";

    public static final String CHAT_BLOCKED = PREFIX + "§cThe chat is currently locked. Please wait......";

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {

        if (!ChatLockCommand.chatLocked) {
            return;
        }

        // Staff with the permission can still chat
        if (event.getPlayer().hasPermission("grassgg.chatlock")) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage(CHAT_BLOCKED);
    }
}