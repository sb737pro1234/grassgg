package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (!MissionWorldUtils.isMissionWorld(event.getBlock().getWorld())) {
            return;
        }

        ProgressManager.handleBlockBreak(
                event.getPlayer(),
                event.getBlock().getType()
        );

    }

}