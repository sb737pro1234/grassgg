package me.sbpro.grassggteleport.listener;

import me.sbpro.grassggteleport.request.TeleportRequestManager;
import me.sbpro.grassggteleport.settings.TeleportPreferencesManager;
import me.sbpro.grassggteleport.spectate.SpectateManager;
import me.sbpro.grassggteleport.teleport.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class TeleportListener implements Listener {

    private final TeleportManager teleportManager;
    private final TeleportRequestManager requestManager;
    private final TeleportPreferencesManager preferencesManager;
    private final SpectateManager spectateManager;

    public TeleportListener(
            TeleportManager teleportManager,
            TeleportRequestManager requestManager,
            TeleportPreferencesManager preferencesManager,
            SpectateManager spectateManager
    ) {
        this.teleportManager = teleportManager;
        this.requestManager = requestManager;
        this.preferencesManager = preferencesManager;
        this.spectateManager = spectateManager;
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!teleportManager.hasActiveTeleport(
                event.getPlayer()
        )) {
            return;
        }

        if (event.getTo() == null) {
            return;
        }

        if (event.getFrom().getX() != event.getTo().getX()
                || event.getFrom().getZ() != event.getTo().getZ()) {
            teleportManager.cancelForMovement(
                    event.getPlayer()
            );
        }
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!teleportManager.hasActiveTeleport(player)) {
            return;
        }

        teleportManager.cancelForDamage(player);
    }

    @EventHandler
    public void onPlayerChangedWorld(
            PlayerChangedWorldEvent event
    ) {
        if (!teleportManager.hasActiveTeleport(
                event.getPlayer()
        )) {
            return;
        }

        teleportManager.cancelForWorldChange(
                event.getPlayer()
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        teleportManager.cleanup(player);
        requestManager.handleDisconnect(player);
        preferencesManager.cleanup(player);
        spectateManager.cleanup(player);
    }
}