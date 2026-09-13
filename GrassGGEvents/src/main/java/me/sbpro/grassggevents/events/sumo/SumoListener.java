package me.sbpro.grassggevents.events.sumo;

import me.sbpro.grassggevents.GrassGGEvents;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SumoListener implements Listener {

    private final GrassGGEvents plugin;

    public SumoListener(GrassGGEvents plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (!event.getBlock().getWorld().getName().equalsIgnoreCase("sumo")) {
            return;
        }

        if (event.getPlayer().hasPermission("grassgg.build")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (!event.getBlock().getWorld().getName().equalsIgnoreCase("sumo")) {
            return;
        }

        if (event.getPlayer().hasPermission("grassgg.build")) {
            return;
        }

        event.setCancelled(true);
    }
}