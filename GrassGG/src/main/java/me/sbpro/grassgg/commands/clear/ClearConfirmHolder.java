package me.sbpro.grassgg.commands.clear;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.entity.Player;

public class ClearConfirmHolder implements InventoryHolder {

    private final Player target;

    public ClearConfirmHolder(Player target) {
        this.target = target;
    }

    public Player getTarget() {
        return target;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}