package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftProgressListener implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!MissionWorldUtils.isMissionWorld(player.getWorld())) {
            return;
        }

        if (event.getRecipe() == null) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();

        // Number of result items crafted
        int amount = result.getAmount();

        // Shift-click crafts many recipes at once
        if (event.isShiftClick()) {

            int max = Integer.MAX_VALUE;

            for (ItemStack item : event.getInventory().getMatrix()) {

                if (item == null) {
                    continue;
                }

                max = Math.min(max, item.getAmount());

            }

            amount *= max;

        }

        ProgressManager.handleCraft(
                player,
                result.getType(),
                amount
        );

    }

}