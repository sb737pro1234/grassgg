package me.sbpro.grassgg.commands.trash;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Marker holder for the "are you sure?" inventory shown after closing the trash GUI.
 * Holds the actual items pending deletion so Confirm/Cancel know what to do with them.
 */
public class ConfirmHolder implements InventoryHolder {

    public static final int SIZE = 27;
    public static final int CONFIRM_SLOT = 16;
    public static final int CANCEL_SLOT = 10;
    public static final int OVERVIEW_SLOT = 13;

    private static final int MAX_LORE_LINES = 20;

    private final Inventory inventory;
    private final List<ItemStack> pendingItems;

    /** True once Confirm/Cancel has been clicked, so the close handler doesn't also return items. */
    private boolean resolved = false;

    public ConfirmHolder(List<ItemStack> pendingItems) {
        this.pendingItems = pendingItems;

        this.inventory = Bukkit.createInventory(
                this,
                SIZE,
                Component.text("Confirm Trash", NamedTextColor.RED)
        );

        ItemStack filler = filler();
        for (int i = 0; i < SIZE; i++) {
            inventory.setItem(i, filler);
        }

        inventory.setItem(CONFIRM_SLOT, confirmButton());
        inventory.setItem(CANCEL_SLOT, cancelButton());
        inventory.setItem(OVERVIEW_SLOT, overviewButton(pendingItems));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public List<ItemStack> getPendingItems() {
        return pendingItems;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    /**
     * Builds and opens the confirmation inventory for the given player.
     */
    public static ConfirmHolder open(Player player, List<ItemStack> pendingItems) {
        ConfirmHolder holder = new ConfirmHolder(pendingItems);
        player.openInventory(holder.getInventory());
        return holder;
    }

    private static ItemStack filler() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(" "));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack confirmButton() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Confirm", NamedTextColor.GREEN, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Click to permanently delete", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("these items.", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack cancelButton() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Cancel", NamedTextColor.RED, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Click to return these items", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("to your inventory.", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack overviewButton(List<ItemStack> items) {
        ItemStack item = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Items to Trash", NamedTextColor.RED, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(buildOverviewLore(items));
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Aggregates the pending items by display name (or material name if unnamed)
     * and renders them as lore lines, e.g. "x64 Diamond Sword".
     */
    private static List<Component> buildOverviewLore(List<ItemStack> items) {
        List<Component> lore = new ArrayList<>();

        if (items.isEmpty()) {
            lore.add(Component.text("- Nothing", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false));
            return lore;
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ItemStack item : items) {
            counts.merge(displayNameOf(item), item.getAmount(), Integer::sum);
        }

        int shown = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (shown >= MAX_LORE_LINES) {
                int remaining = counts.size() - shown;
                lore.add(Component.text("...and " + remaining + " more type(s)", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
                break;
            }
            lore.add(Component.text("- x" + entry.getValue() + " " + entry.getKey(), NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            shown++;
        }

        return lore;
    }

    @SuppressWarnings("deprecation") // ItemMeta#getDisplayName() - simplest way to get plain text
    private static String displayNameOf(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            String plain = ChatColor.stripColor(meta.getDisplayName());
            if (plain != null && !plain.isBlank()) {
                return plain;
            }
        }
        return prettify(item.getType());
    }

    private static String prettify(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(' ');
        }
        return sb.toString().trim();
    }
}