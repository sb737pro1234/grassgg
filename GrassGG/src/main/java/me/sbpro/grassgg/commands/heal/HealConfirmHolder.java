package me.sbpro.grassgg.commands.heal;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class HealConfirmHolder implements InventoryHolder {

    private final Player target;

    public HealConfirmHolder(Player target) {
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