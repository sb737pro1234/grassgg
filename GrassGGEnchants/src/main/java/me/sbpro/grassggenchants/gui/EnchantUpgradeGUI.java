package me.sbpro.grassggenchants.gui;

import me.sbpro.grassggenchants.GrassGGEnchants;
import me.sbpro.grassggenchants.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EnchantUpgradeGUI {

    private EnchantUpgradeGUI() {
    }

    public static void openSelection(Player player, ItemStack target, int targetSlot) {
        List<Enchantment> eligible = getEligibleEnchantments(target);

        Inventory inventory = Bukkit.createInventory(
                new SelectionHolder(target, targetSlot),
                27,
                Messages.ENCHANT_SELECT_TITLE
        );

        int slot = 10;
        for (Enchantment enchantment : eligible) {
            if (slot == 17) {
                slot = 19;
            }

            ItemStack display = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = display.getItemMeta();

            int current = target.getEnchantmentLevel(enchantment);
            int next = current + 1;

            meta.setDisplayName(Messages.CUSTOM + formatEnchantment(enchantment) + " " + current);
            meta.setLore(List.of(
                    Messages.GRAY + "Upgrade to " + Messages.WHITE + formatEnchantment(enchantment) + " " + next,
                    Messages.GRAY + "Click to select."
            ));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            display.setItemMeta(meta);
            inventory.setItem(slot, display);
            slot++;
        }

        player.openInventory(inventory);
    }

    public static void openConfirm(Player player, ItemStack target, int targetSlot, Enchantment enchantment) {
        Inventory inventory = Bukkit.createInventory(
                new ConfirmationHolder(target, targetSlot, enchantment),
                27,
                Messages.CONFIRM_TITLE
        );

        ItemStack confirm = pane(Material.LIME_STAINED_GLASS_PANE, Messages.CONFIRM_NAME, Messages.CONFIRM_LORE);
        ItemStack cancel = pane(Material.RED_STAINED_GLASS_PANE, Messages.CANCEL_NAME, Messages.CANCEL_LORE);

        ItemStack display = target.clone();
        ItemMeta meta = display.getItemMeta();

        int current = target.getEnchantmentLevel(enchantment);
        int next = current + 1;

        meta.setDisplayName(Messages.CUSTOM + formatEnchantment(enchantment) + " " + current + " → " + next);
        meta.setLore(List.of(Messages.format(
                Messages.DISPLAY_ITEM_LORE,
                "%enchant%", formatEnchantment(enchantment),
                "%current%", String.valueOf(current),
                "%next%", String.valueOf(next)
        )));
        display.setItemMeta(meta);

        inventory.setItem(16, confirm);
        inventory.setItem(10, cancel);
        inventory.setItem(13, display);

        player.openInventory(inventory);
    }

    private static ItemStack pane(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static List<Enchantment> getEligibleEnchantments(ItemStack item) {
        List<Enchantment> eligible = new ArrayList<>();

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return eligible;
        }

        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (!Messages.UPGRADEABLE_ENCHANTMENTS.contains(enchantment)) {
                continue;
            }

            int maxLevel = enchantment.getMaxLevel();

            if (maxLevel < 2) {
                continue;
            }

            if (level != maxLevel) {
                continue;
            }

            if (!enchantment.canEnchantItem(item)) {
                continue;
            }

            eligible.add(enchantment);
        }

        return eligible;
    }

    public static String formatEnchantment(Enchantment enchantment) {
        String key = enchantment.getKey().getKey().replace('_', ' ');
        String[] words = key.split(" ");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (!result.isEmpty()) {
                result.append(' ');
            }

            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }

        return result.toString();
    }

    public static final class SelectionHolder implements org.bukkit.inventory.InventoryHolder {
        private final ItemStack target;
        private final int targetSlot;

        public SelectionHolder(ItemStack target, int targetSlot) {
            this.target = target.clone();
            this.targetSlot = targetSlot;
        }

        public ItemStack getTarget() {
            return target;
        }

        public int getTargetSlot() {
            return targetSlot;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static final class ConfirmationHolder implements org.bukkit.inventory.InventoryHolder {
        private final ItemStack target;
        private final int targetSlot;
        private final Enchantment enchantment;

        public ConfirmationHolder(ItemStack target, int targetSlot, Enchantment enchantment) {
            this.target = target.clone();
            this.targetSlot = targetSlot;
            this.enchantment = enchantment;
        }

        public ItemStack getTarget() {
            return target;
        }

        public int getTargetSlot() {
            return targetSlot;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
