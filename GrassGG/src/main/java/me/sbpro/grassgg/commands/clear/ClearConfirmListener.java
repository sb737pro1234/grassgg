package me.sbpro.grassgg.commands.clear;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class ClearConfirmListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof ClearConfirmHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();

        // Cancel
        if (slot == 10) {
            player.closeInventory();
            return;
        }

        // Confirm
        if (slot == 16) {

            Player target = holder.getTarget();

            // Target left while menu was open
            if (!target.isOnline()) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "That player is no longer online.");
                return;
            }

            // Clear the inventory
            target.getInventory().clear();

            player.closeInventory();

            if (target.equals(player)) {
                player.sendMessage(
                        "§2§lGRASS.GG §8» §fYour inventory has been cleared."
                );
            } else {
                player.sendMessage(
                        "§2§lGRASS.GG §8» §fCleared §2" + target.getName() + "§f's inventory."
                );

                target.sendMessage(
                        "§2§lGRASS.GG §8» §fYour inventory has been cleared by §2" + player.getName() + "§f."
                );
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (event.getInventory().getHolder() instanceof ClearConfirmHolder) {
            event.setCancelled(true);
        }
    }
}