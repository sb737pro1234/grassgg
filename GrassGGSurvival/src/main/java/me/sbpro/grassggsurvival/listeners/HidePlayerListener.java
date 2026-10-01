package me.sbpro.grassggsurvival.listeners;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HidePlayerListener implements Listener {

    private final GrassGGSurvival plugin;

    public HidePlayerListener(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    /**
     * When a new player joins:
     * - Hide them from anyone who has hide mode ON
     * - If the joining player has hide mode ON, hide everyone from them too
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joining = event.getPlayer();

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(joining)) continue;

            // If an existing player has hide mode ON, hide the new joiner from them
            if (plugin.isHidingPlayers(online.getUniqueId())) {
                online.hidePlayer(plugin, joining);
            }

            // If the joining player has hide mode ON (e.g., persisted via data or re-login edge case),
            // hide all existing players from them
            if (plugin.isHidingPlayers(joining.getUniqueId())) {
                joining.hidePlayer(plugin, online);
            }
        }
    }

    /**
     * When a player leaves:
     * - Remove them from the hidden set so there's no stale data
     * - No need to explicitly show them since they're gone; Bukkit cleans up visibility state on disconnect
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player leaving = event.getPlayer();
        // Clean up their hide state on disconnect
        plugin.setHidingPlayers(leaving.getUniqueId(), false);
    }
}