package me.sbpro.grassggcoins.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ConfirmationMenuHolder implements InventoryHolder {

    private final String identifier;
    private Inventory inventory;

    public ConfirmationMenuHolder(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}