package me.sbpro.grassggstaff.commands.clearchat;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClearChatConfirmHolder
        implements InventoryHolder {

    private Inventory inventory;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}