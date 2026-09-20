package me.sbpro.grassgg.commands.trash;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Marker holder for the "drop items here to trash them" inventory.
 * Its presence on an Inventory is how the listener recognizes this GUI.
 */
public class TrashHolder implements InventoryHolder {

    public static final int SIZE = 54;

    /** Bottom-right slot: clicking it opens the confirm menu, same as closing the GUI. */
    public static final int BUTTON_SLOT = SIZE - 1;

    private final Inventory inventory;

    public TrashHolder() {
        // Build the inventory in the constructor so getInventory() is never null,
        // and so `this` is fully valid the moment Bukkit hands it back to us.
        this.inventory = Bukkit.createInventory(
                this,
                SIZE,
                Component.text("Trash", NamedTextColor.DARK_GRAY)
        );
        this.inventory.setItem(BUTTON_SLOT, triggerButton());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Whether this slot is a reserved GUI slot (not a spot for player items),
     * and should therefore be skipped when collecting items to trash.
     */
    public static boolean isReservedSlot(int slot) {
        return slot == BUTTON_SLOT;
    }

    /**
     * Opens a fresh trash inventory for the given player.
     */
    public static void open(Player player) {
        TrashHolder holder = new TrashHolder();
        player.openInventory(holder.getInventory());
    }

    private static ItemStack triggerButton() {
        ItemStack item = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Confirm Trash", NamedTextColor.RED, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
                Component.text("Click to review and confirm", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("what you've put in here.", NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)
        ));
        item.setItemMeta(meta);
        return item;
    }
}