package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class AnimalListener implements Listener {

    @EventHandler
    public void onBreed(EntityBreedEvent event) {

        if (!(event.getMother() instanceof Animals)) {
            return;
        }

        if (!MissionWorldUtils.isMissionWorld(event.getMother().getWorld())) {
            return;
        }

        LivingEntity breeder = event.getBreeder();

        if (!(breeder instanceof Player player)) {
            return;
        }

        ProgressManager.handleBreed(
                player,
                event.getMother().getType()
        );

    }

}