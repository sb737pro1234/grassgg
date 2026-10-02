package me.sbpro.grassggenchants.listeners;

import me.sbpro.grassggenchants.GrassGGEnchants;
import me.sbpro.grassggenchants.Messages;
import me.sbpro.grassggenchants.gui.EnchantUpgradeGUI;
import me.sbpro.grassggenchants.items.EnchantUpgradeItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class EnchantUpgradeListener implements Listener {

    private final GrassGGEnchants plugin;

    public EnchantUpgradeListener(GrassGGEnchants plugin) {
        this.plugin = plugin;
    }

    /**
     * Apply an Enchant Upgrade by picking it up with the cursor and clicking
     * the item that should be upgraded, just like swapping two inventory items.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        // First handle the actual application of the Enchant Upgrade.
        if (event.getClickedInventory() == player.getInventory()
                && event.getCursor() != null
                && EnchantUpgradeItem.isEnchantUpgrade(plugin, event.getCursor())) {

            ItemStack target = event.getCurrentItem();
            if (target == null || target.getType() == Material.AIR) {
                return;
            }

            List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(target);
            if (eligible.isEmpty()) {
                event.setCancelled(true);
                player.sendMessage(Messages.NO_ENCHANTMENTS);
                playError(player);
                return;
            }

            event.setCancelled(true);
            EnchantUpgradeGUI.openSelection(player, target, event.getSlot());
            return;
        }

        if (event.getInventory().getHolder() instanceof EnchantUpgradeGUI.SelectionHolder holder) {
            event.setCancelled(true);

            if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getInventory().getSize()) {
                return;
            }

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(holder.getTarget());
            int slot = event.getRawSlot();

            int index = -1;
            if (slot >= 10 && slot <= 16) {
                index = slot - 10;
            } else if (slot >= 19 && slot <= 25) {
                index = slot - 19 + 7;
            }

            if (index < 0 || index >= eligible.size()) {
                return;
            }

            Enchantment selected = eligible.get(index);
            EnchantUpgradeGUI.openConfirm(player, holder.getTarget(), holder.getTargetSlot(), selected);
            return;
        }

        if (event.getInventory().getHolder() instanceof EnchantUpgradeGUI.ConfirmationHolder holder) {
            event.setCancelled(true);

            if (event.getRawSlot() == 10) {
                player.closeInventory();
                return;
            }

            if (event.getRawSlot() != 16) {
                return;
            }

            ItemStack target = holder.getTarget();
            Enchantment enchantment = holder.getEnchantment();

            List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(target);
            if (!eligible.contains(enchantment)) {
                player.closeInventory();
                player.sendMessage(Messages.NO_ENCHANTMENTS);
                playError(player);
                return;
            }

            int targetSlot = holder.getTargetSlot();
            ItemStack actualTarget = getInventoryItem(player.getInventory(), targetSlot);

            if (!sameItem(actualTarget, target)) {
                player.closeInventory();
                player.sendMessage(Messages.ERROR + "The item you selected is no longer in that inventory slot.");
                playError(player);
                return;
            }

            ItemStack upgradeItem = player.getItemOnCursor();
            if (!EnchantUpgradeItem.isEnchantUpgrade(plugin, upgradeItem)) {
                player.closeInventory();
                player.sendMessage(Messages.ERROR + "You no longer have an Enchant Upgrade selected.");
                playError(player);
                return;
            }

            int current = actualTarget.getEnchantmentLevel(enchantment);
            int max = enchantment.getMaxLevel();

            if (current != max) {
                player.closeInventory();
                player.sendMessage(Messages.NO_ENCHANTMENTS);
                playError(player);
                return;
            }

            actualTarget.addUnsafeEnchantment(enchantment, current + 1);
            player.getInventory().setItem(targetSlot, actualTarget);

            if (upgradeItem.getAmount() <= 1) {
                player.setItemOnCursor(null);
            } else {
                upgradeItem.setAmount(upgradeItem.getAmount() - 1);
                player.setItemOnCursor(upgradeItem);
            }

            player.closeInventory();
            player.sendMessage(Messages.format(
                    Messages.UPGRADED,
                    "%enchant%", EnchantUpgradeGUI.formatEnchantment(enchantment),
                    "%current%", String.valueOf(current),
                    "%next%", String.valueOf(current + 1)
            ));
            player.playSound(player.getLocation(), parseSound(Messages.SUCCESS_SOUND), Messages.SOUND_VOLUME, Messages.SUCCESS_PITCH);
        }
    }

    /**
     * Also supports an actual inventory drag event. If the Enchant Upgrade is
     * being dragged onto an inventory slot containing an eligible item, the
     * normal selection GUI is opened instead of moving the item.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!EnchantUpgradeItem.isEnchantUpgrade(plugin, event.getOldCursor())) {
            return;
        }

        Inventory top = event.getView().getTopInventory();
        if (event.getRawSlots().stream().noneMatch(slot -> slot >= top.getSize())) {
            return;
        }

        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < top.getSize()) {
                continue;
            }

            int playerSlot = rawSlot - top.getSize();
            if (playerSlot < 0 || playerSlot >= player.getInventory().getSize()) {
                continue;
            }

            ItemStack target = player.getInventory().getItem(playerSlot);
            if (target == null || target.getType() == Material.AIR) {
                continue;
            }

            List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(target);
            if (eligible.isEmpty()) {
                event.setCancelled(true);
                player.sendMessage(Messages.NO_ENCHANTMENTS);
                playError(player);
                return;
            }

            event.setCancelled(true);
            EnchantUpgradeGUI.openSelection(player, target, playerSlot);
            return;
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        // Nothing is consumed when either GUI is closed.
    }

    private static ItemStack getInventoryItem(Inventory inventory, int slot) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return null;
        }
        return inventory.getItem(slot);
    }

    private static boolean sameItem(ItemStack first, ItemStack second) {
        if (first == null || second == null || first.getType() == Material.AIR || second.getType() == Material.AIR) {
            return false;
        }
        return first.equals(second);
    }

    private static void playError(Player player) {
        player.playSound(player.getLocation(), parseSound(Messages.ERROR_SOUND), Messages.SOUND_VOLUME, Messages.ERROR_PITCH);
    }

    private static Sound parseSound(String name) {
        try {
            return Sound.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return Sound.ENTITY_VILLAGER_NO;
        }
    }
}
