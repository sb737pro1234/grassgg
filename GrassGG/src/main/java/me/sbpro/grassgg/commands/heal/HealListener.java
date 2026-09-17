package me.sbpro.grassgg.commands.heal;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class HealListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder() instanceof HealConfirmHolder holder)) {
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

            target.setHealth(target.getAttribute(Attribute.MAX_HEALTH).getValue());
            target.setFoodLevel(20);
            target.setSaturation(20);
            target.setFireTicks(0);

            player.closeInventory();

            if (target.equals(player)) {
                player.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2Your §fhealth has been restored."
                );
            } else {
                player.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2" + target.getName() + "§f's health has been restored."
                );

                target.sendMessage(
                        "§x§E§F§4§4§4§4§lADMIN §8» §2Your §fhealth has been restored by §2" + player.getName() + "§f."
                );
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (event.getInventory().getHolder() instanceof HealConfirmHolder) {
            event.setCancelled(true);
        }
    }
}