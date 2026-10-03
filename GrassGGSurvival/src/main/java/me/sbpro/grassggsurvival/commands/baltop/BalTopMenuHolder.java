package me.sbpro.grassggsurvival.commands.baltop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BalTopMenuHolder implements InventoryHolder {

    private Inventory inventory;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}