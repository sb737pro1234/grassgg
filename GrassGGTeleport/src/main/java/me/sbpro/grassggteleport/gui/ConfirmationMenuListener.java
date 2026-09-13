package me.sbpro.grassggteleport.gui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class ConfirmationMenuListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(ConfirmationMenu.TITLE)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot == ConfirmationMenu.CONFIRM_SLOT) {
            ConfirmationAction action = ConfirmationMenu.removeAction(player);

            if (action == null) {
                player.closeInventory();
                return;
            }

            player.playSound(
                    player.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    1.0f,
                    1.0f
            );

            player.closeInventory();

            action.confirm();
            return;
        }

        if (slot == ConfirmationMenu.CANCEL_SLOT) {
            ConfirmationAction action = ConfirmationMenu.removeAction(player);

            if (action == null) {
                player.closeInventory();
                return;
            }

            player.playSound(
                    player.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    1.0f,
                    0.8f
            );

            player.closeInventory();

            action.cancel();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(ConfirmationMenu.TITLE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(ConfirmationMenu.TITLE)) {
            return;
        }

        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        ConfirmationAction action = ConfirmationMenu.removeAction(player);

        if (action != null) {
            action.cancel();
        }
    }
}