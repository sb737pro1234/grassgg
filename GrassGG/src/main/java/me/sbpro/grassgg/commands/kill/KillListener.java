package me.sbpro.grassgg.commands.kill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class KillListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof KillConfirmHolder holder)) {
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

            target.setHealth(0);

            player.closeInventory();

            if (target.equals(player)) {
                player.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2You §fhave been killed."
                );
            } else {
                player.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2" + target.getName() + "§f has been killed."
                );

                target.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2You §fhave been killed by §2" + player.getName() + "§f."
                );
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (event.getInventory().getHolder() instanceof KillConfirmHolder) {
            event.setCancelled(true);
        }
    }
}