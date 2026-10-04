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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class EnchantUpgradeListener implements Listener {

    private final GrassGGEnchants plugin;

    public EnchantUpgradeListener(GrassGGEnchants plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!EnchantUpgradeItem.isEnchantUpgrade(plugin, item)) return;

        event.setCancelled(true);
        EnchantUpgradeGUI.open(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory top = event.getView().getTopInventory();

        if (top.getHolder() instanceof EnchantUpgradeGUI.UpgradeHolder holder) {
            handleUpgradeMenu(event, player, holder);
            return;
        }

        if (top.getHolder() instanceof EnchantUpgradeGUI.SelectionHolder holder) {
            handleSelectionMenu(event, player, holder);
            return;
        }

        if (top.getHolder() instanceof EnchantUpgradeGUI.ConfirmationHolder holder) {
            handleConfirmationMenu(event, player, holder);
        }
    }

    private void handleUpgradeMenu(InventoryClickEvent event, Player player, EnchantUpgradeGUI.UpgradeHolder holder) {
        event.setCancelled(true);

        if (event.getClickedInventory() == null) return;

        // Never allow shift-click, number-key swapping, double-click collecting,
        // or offhand swapping to interact with the custom input slots.
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT
                || event.getClick() == ClickType.NUMBER_KEY || event.getClick() == ClickType.DOUBLE_CLICK
                || event.getClick() == ClickType.SWAP_OFFHAND) {
            return;
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot >= event.getView().getTopInventory().getSize()) {
            // Let normal clicks in the player's inventory pick an item onto the cursor.
            if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
                event.setCancelled(false);
            }
            return;
        }

        if (rawSlot != 11 && rawSlot != 15) return;

        ItemStack cursor = event.getCursor();
        boolean upgradeSlot = rawSlot == 15;

        if (cursor != null && cursor.getType() != Material.AIR) {
            if (upgradeSlot) {
                if (!EnchantUpgradeItem.isEnchantUpgrade(plugin, cursor)) {
                    error(player, Messages.NOT_ENCHANTED_ITEM);
                    return;
                }

                if (holder.getUpgrade() != null) return;

                // Consume exactly ONE from the cursor and store exactly ONE.
                ItemStack one = cursor.clone();
                one.setAmount(1);
                holder.setUpgrade(one);
                ItemStack remaining = cursor.clone();
                if (remaining.getAmount() <= 1) event.getView().setCursor(null);
                else {
                    remaining.setAmount(remaining.getAmount() - 1);
                    event.getView().setCursor(remaining);
                }
            } else {
                if (EnchantUpgradeItem.isEnchantUpgrade(plugin, cursor)) {
                    error(player, Messages.NOT_ENCHANTED_ITEM);
                    return;
                }
                if (cursor.getAmount() > 1) {
                    error(player, Messages.NOT_ENCHANTED_ITEM);
                    return;
                }
                if (holder.getTarget() != null) return;

                holder.setTarget(cursor.clone());
                event.getView().setCursor(null);
            }

            EnchantUpgradeGUI.refresh(holder);
            tryOpenSelection(player, holder);
            return;
        }

        // Empty cursor: pick the stored item back up.
        if (upgradeSlot && holder.getUpgrade() != null) {
            event.getView().setCursor(holder.getUpgrade().clone());
            holder.setUpgrade(null);
        } else if (!upgradeSlot && holder.getTarget() != null) {
            event.getView().setCursor(holder.getTarget().clone());
            holder.setTarget(null);
        }
        EnchantUpgradeGUI.refresh(holder);
    }

    private void tryOpenSelection(Player player, EnchantUpgradeGUI.UpgradeHolder holder) {
        if (holder.getTarget() == null || holder.getUpgrade() == null) return;

        List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(holder.getTarget());
        if (eligible.isEmpty()) {
            error(player, Messages.NO_ENCHANTMENTS);
            return;
        }

        holder.setTransitioning(true);
        ItemStack target = holder.getTarget().clone();
        ItemStack upgrade = holder.getUpgrade().clone();
        holder.setTarget(null);
        holder.setUpgrade(null);
        player.getOpenInventory().getTopInventory().clear();
        EnchantUpgradeGUI.openSelection(player, target, upgrade);
    }

    private void handleSelectionMenu(InventoryClickEvent event, Player player, EnchantUpgradeGUI.SelectionHolder holder) {
        event.setCancelled(true);
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();
        if (slot < 10 || slot > 25) return;
        if (slot == 17 || slot == 18) return;

        List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(holder.getTarget());
        int index = slot <= 16 ? slot - 10 : slot - 19 + 7;
        if (index < 0 || index >= eligible.size()) return;

        holder.setTransitioning(true);
        EnchantUpgradeGUI.openConfirm(player, holder.getTarget(), holder.getUpgrade(), eligible.get(index));
    }

    private void handleConfirmationMenu(InventoryClickEvent event, Player player, EnchantUpgradeGUI.ConfirmationHolder holder) {
        event.setCancelled(true);
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        if (event.getRawSlot() == 10) {
            returnItems(player, holder.getTarget(), holder.getUpgrade());
            holder.setTransitioning(true);
            player.closeInventory();
            return;
        }

        if (event.getRawSlot() != 16) return;

        ItemStack target = holder.getTarget().clone();
        Enchantment enchantment = holder.getEnchantment();
        List<Enchantment> eligible = EnchantUpgradeGUI.getEligibleEnchantments(target);
        if (!eligible.contains(enchantment)) {
            error(player, Messages.NO_ENCHANTMENTS);
            returnItems(player, target, holder.getUpgrade());
            holder.setTransitioning(true);
            player.closeInventory();
            return;
        }

        int current = target.getEnchantmentLevel(enchantment);
        if (current != enchantment.getMaxLevel()) {
            error(player, Messages.NO_ENCHANTMENTS);
            returnItems(player, target, holder.getUpgrade());
            holder.setTransitioning(true);
            player.closeInventory();
            return;
        }

        target.addUnsafeEnchantment(enchantment, current + 1);
        returnItems(player, target, null);
        holder.setTransitioning(true);
        player.closeInventory();
        player.sendMessage(Messages.format(Messages.UPGRADED,
                "%enchant%", EnchantUpgradeGUI.formatEnchantment(enchantment),
                "%current%", String.valueOf(current),
                "%next%", String.valueOf(current + 1)));
        player.playSound(player.getLocation(), parseSound(Messages.SUCCESS_SOUND), Messages.SOUND_VOLUME, Messages.SUCCESS_PITCH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getView().getTopInventory().getHolder() instanceof EnchantUpgradeGUI.UpgradeHolder)) return;

        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (event.getInventory().getHolder() instanceof EnchantUpgradeGUI.UpgradeHolder holder) {
            if (holder.isTransitioning()) return;
            returnItems(player, holder.getTarget(), holder.getUpgrade());
            holder.setTarget(null);
            holder.setUpgrade(null);
            return;
        }

        if (event.getInventory().getHolder() instanceof EnchantUpgradeGUI.SelectionHolder holder) {
            if (holder.isTransitioning()) return;
            returnItems(player, holder.getTarget(), holder.getUpgrade());
            return;
        }

        if (event.getInventory().getHolder() instanceof EnchantUpgradeGUI.ConfirmationHolder holder) {
            if (holder.isTransitioning()) return;
            returnItems(player, holder.getTarget(), holder.getUpgrade());
        }
    }

    private void returnItems(Player player, ItemStack target, ItemStack upgrade) {
        if (target != null && target.getType() != Material.AIR) giveOrDrop(player, target);
        if (upgrade != null && upgrade.getType() != Material.AIR) giveOrDrop(player, upgrade);
    }

    private void giveOrDrop(Player player, ItemStack item) {
        player.getInventory().addItem(item).forEach((slot, leftover) ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    private void error(Player player, String message) {
        player.sendMessage(message);
        player.playSound(player.getLocation(), parseSound(Messages.ERROR_SOUND), Messages.SOUND_VOLUME, Messages.ERROR_PITCH);
    }

    private static Sound parseSound(String name) {
        try { return Sound.valueOf(name); }
        catch (IllegalArgumentException exception) { return Sound.ENTITY_VILLAGER_NO; }
    }
}
