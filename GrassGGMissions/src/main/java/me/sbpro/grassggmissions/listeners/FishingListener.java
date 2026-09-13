package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {

    @EventHandler
    public void onFish(PlayerFishEvent event) {

        if (!MissionWorldUtils.isMissionWorld(event.getPlayer().getWorld())) {
            return;
        }

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        if (!(event.getCaught() instanceof Item item)) {
            return;
        }

        Material caught = item.getItemStack().getType();

        switch (caught) {

            case COD,
                 SALMON,
                 TROPICAL_FISH,
                 PUFFERFISH -> ProgressManager.handleFish(event.getPlayer());

            default -> {
                // Treasure and junk do not count
            }

        }

    }

}