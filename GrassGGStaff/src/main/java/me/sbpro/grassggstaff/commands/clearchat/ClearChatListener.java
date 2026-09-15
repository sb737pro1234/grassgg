package me.sbpro.grassggstaff.commands.clearchat;

import me.sbpro.grassggstaff.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.entity.Player;

public final class ClearChatListener
        implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent event
    ) {

        if (!(event.getView()
                .getTopInventory()
                .getHolder()
                instanceof ClearChatConfirmHolder)) {

            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        int slot =
                event.getRawSlot();

        /*
         * Only react to clicks inside the
         * top inventory.
         */
        if (slot < 0
                || slot >= event.getView()
                .getTopInventory()
                .getSize()) {

            return;
        }

        /*
         * Slot 16 = CONFIRM
         */
        if (slot == 16) {

            player.closeInventory();

            ClearChatCommand.clearChat(
                    player.getName()
            );

            return;
        }

        /*
         * Slot 10 = CANCEL
         */
        if (slot == 10) {

            player.closeInventory();
        }
    }

    @EventHandler
    public void onDrag(
            InventoryDragEvent event
    ) {

        if (event.getView()
                .getTopInventory()
                .getHolder()
                instanceof ClearChatConfirmHolder) {

            event.setCancelled(true);
        }
    }
}