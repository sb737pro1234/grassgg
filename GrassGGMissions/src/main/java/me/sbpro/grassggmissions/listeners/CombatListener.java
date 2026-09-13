package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class CombatListener implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent event) {

        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        if (!MissionWorldUtils.isMissionWorld(killer.getWorld())) {
            return;
        }

        ProgressManager.handleMobKill(
                killer,
                event.getEntityType()
        );

    }

}