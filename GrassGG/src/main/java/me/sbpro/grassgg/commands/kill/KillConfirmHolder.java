package me.sbpro.grassgg.commands.kill;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class KillConfirmHolder implements InventoryHolder {

    private final Player target;

    public KillConfirmHolder(Player target) {
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