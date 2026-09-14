package me.sbpro.grassggstaff.staffchat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public final class StaffChatListener
        implements Listener {

    private final StaffChatManager manager;

    public StaffChatListener(
            StaffChatManager manager
    ) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(
            AsyncChatEvent event
    ) {

        Player player =
                event.getPlayer();

        /*
         * Players without the permission can never
         * use staff chat even if somehow toggled.
         */
        if (!player.hasPermission(
                "grassgg.staff.chat"
        )) {

            manager.disable(player);

            return;
        }

        /*
         * Staff chat is not enabled.
         * Let the normal chat system handle it.
         */
        if (!manager.isEnabled(player)) {
            return;
        }

        /*
         * Stop the message from entering normal
         * public chat.
         */
        event.setCancelled(true);

        manager.sendStaffChat(event);
    }
}