package me.sbpro.grassggstaff.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.punishment.OffenceRecord;
import me.sbpro.grassggstaff.punishment.PunishmentEnforcementManager;
import me.sbpro.grassggstaff.punishment.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public final class PunishmentChatListener implements Listener {

    private final GrassGGStaff plugin;
    private final PunishmentEnforcementManager enforcement;

    public PunishmentChatListener(
            GrassGGStaff plugin,
            PunishmentEnforcementManager enforcement
    ) {
        this.plugin = plugin;
        this.enforcement = enforcement;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {

        Player player = event.getPlayer();

        OffenceRecord punishment =
                enforcement.getActive(player.getUniqueId());

        if (punishment == null) {
            return;
        }

        if (punishment.punishmentType() != PunishmentType.MUTE) {
            return;
        }

        event.setCancelled(true);

        player.sendMessage(
                Messages.muted(punishment)
        );
    }
}