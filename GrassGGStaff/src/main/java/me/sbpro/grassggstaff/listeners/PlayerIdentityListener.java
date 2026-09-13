package me.sbpro.grassggstaff.listeners;

import me.sbpro.grassggstaff.GrassGGStaff;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerIdentityListener
        implements Listener {

    private final GrassGGStaff plugin;

    public PlayerIdentityListener(
            GrassGGStaff plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(
            PlayerJoinEvent event
    ) {

        plugin.getPlayerRepository()
                .savePlayer(
                        event.getPlayer().getUniqueId(),
                        event.getPlayer().getName()
                )
                .exceptionally(exception -> {

                    plugin.getLogger().warning(
                            "Failed to save player identity for "
                                    + event.getPlayer().getName()
                                    + ": "
                                    + exception.getMessage()
                    );

                    return null;
                });
    }
}