package me.sbpro.grassggstaff.listeners;

import me.sbpro.grassggstaff.GrassGGStaff;
import me.sbpro.grassggstaff.Messages;
import me.sbpro.grassggstaff.punishment.OffenceRecord;
import me.sbpro.grassggstaff.punishment.PunishmentEnforcementManager;
import me.sbpro.grassggstaff.punishment.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;

public final class PunishmentLoginListener implements Listener {

    private final GrassGGStaff plugin;
    private final PunishmentEnforcementManager enforcement;

    public PunishmentLoginListener(
            GrassGGStaff plugin,
            PunishmentEnforcementManager enforcement
    ) {
        this.plugin = plugin;
        this.enforcement = enforcement;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {

        OffenceRecord punishment =
                plugin.getOffenceRepository()
                        .findActivePunishment(event.getUniqueId())
                        .join();

        /*
         * No active punishment.
         */
        if (punishment == null) {
            enforcement.invalidate(event.getUniqueId());
            return;
        }

        /*
         * Store it immediately so chat enforcement is ready
         * as soon as the player joins.
         */
        enforcement.refresh(event.getUniqueId());

        /*
         * Expired punishment.
         */
        if (punishment.expiresAt() != null &&
                punishment.expiresAt().isBefore(Instant.now())) {

            return;
        }

        /*
         * Warnings don't prevent joining.
         */
        if (punishment.punishmentType() != PunishmentType.BAN) {
            return;
        }

        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                Messages.banScreen(punishment)
        );
    }
}