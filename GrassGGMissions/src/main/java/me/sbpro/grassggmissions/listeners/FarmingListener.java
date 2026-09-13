package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class FarmingListener implements Listener {

    @EventHandler
    public void onHarvest(BlockBreakEvent event) {

        if (!MissionWorldUtils.isMissionWorld(event.getBlock().getWorld())) {
            return;
        }

        if (event.getBlock().getType() != Material.WHEAT) {
            return;
        }

        if (!(event.getBlock().getBlockData() instanceof Ageable ageable)) {
            return;
        }

        // Only count fully-grown wheat
        if (ageable.getAge() != ageable.getMaximumAge()) {
            return;
        }

        ProgressManager.handleHarvest(
                event.getPlayer(),
                Material.WHEAT
        );

    }

    @EventHandler
    public void onPlant(BlockPlaceEvent event) {

        if (!MissionWorldUtils.isMissionWorld(event.getBlock().getWorld())) {
            return;
        }

        if (event.getBlockPlaced().getType() != Material.WHEAT) {
            return;
        }

        ProgressManager.handlePlant(
                event.getPlayer(),
                Material.WHEAT_SEEDS
        );

    }

}