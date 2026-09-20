package me.sbpro.grassgg.commands.trash;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TrashListener implements Listener {

    private final JavaPlugin plugin;

    public TrashListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // --- Trash GUI: closing it collects the items and opens the confirm GUI ---

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof TrashHolder)) {
            return;
        }
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        List<ItemStack> collected = new ArrayList<>();
        ItemStack[] contents = event.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            if (TrashHolder.isReservedSlot(slot)) {
                continue; // the lava bucket trigger button, not a player item
            }
            ItemStack item = contents[slot];
            if (item != null && !item.getType().isAir()) {
                collected.add(item.clone());
            }
        }

        // Wipe the trash inventory now - these items are no longer anywhere
        // except in our "collected" list until the player confirms or cancels.
        event.getInventory().clear();

        if (collected.isEmpty()) {
            return;
        }

        // Use the player's own scheduler (not Bukkit's global one) so this is
        // guaranteed to run on the correct thread/region for that player.
        player.getScheduler().run(plugin, task -> {
            if (player.isOnline()) {
                ConfirmHolder.open(player, collected);
            }
        }, null);
    }

    // --- Trash GUI: clicking the lava bucket button is the same as closing it ---

    @EventHandler
    public void onTrashClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof TrashHolder)) {
            return;
        }

        Inventory clicked = event.getClickedInventory();
        if (clicked == null || !clicked.equals(event.getView().getTopInventory())) {
            return; // clicked their own inventory - normal item movement is fine
        }

        if (!TrashHolder.isReservedSlot(event.getSlot())) {
            return; // any other slot - normal item movement is fine
        }

        // Never let the button itself be taken, swapped, or overwritten.
        event.setCancelled(true);

        if (event.getWhoClicked() instanceof Player player) {
            player.closeInventory(); // reuses the onInventoryClose flow above
        }
    }

    // --- Confirm GUI: lock it down and handle the two buttons ---

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof ConfirmHolder confirmHolder)) {
            return; // not our GUI (trash GUI itself allows normal item movement)
        }

        // Lock the whole view - no taking/moving items while confirming.
        event.setCancelled(true);

        Inventory clicked = event.getClickedInventory();
        if (clicked == null || !clicked.equals(event.getView().getTopInventory())) {
            return; // clicked their own inventory - ignore
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        switch (event.getSlot()) {
            case ConfirmHolder.CONFIRM_SLOT -> {
                confirmHolder.setResolved(true);
                player.closeInventory();
                player.sendMessage(Component.text("§2§lGRASS.GG §8» §fTrashed §2" + confirmHolder.getPendingItems().size()
                        + "§f item stack(s)."));
            }
            case ConfirmHolder.CANCEL_SLOT -> {
                confirmHolder.setResolved(true);
                returnItems(player, confirmHolder.getPendingItems());
                player.closeInventory();
                player.sendMessage(Component.text("§2§lGRASS.GG §8» §cCancelled - items returned."));
            }
            default -> {
                // clicked a filler slot / overview item - do nothing
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof ConfirmHolder) {
            event.setCancelled(true);
        }
    }

    // Pressing Escape (or otherwise closing) without clicking a button counts as Cancel.
    @EventHandler
    public void onConfirmClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof ConfirmHolder confirmHolder)) {
            return;
        }
        if (confirmHolder.isResolved()) {
            return; // already handled by the click handler
        }
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        confirmHolder.setResolved(true);
        returnItems(player, confirmHolder.getPendingItems());
        player.sendMessage(Component.text("§2§lGRASS.GG §8» §cCancelled - items returned."));
    }

    private void returnItems(Player player, List<ItemStack> items) {
        List<ItemStack> leftovers = new ArrayList<>(player.getInventory().addItem(
                items.toArray(new ItemStack[0])
        ).values());

        for (ItemStack leftover : leftovers) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }
}