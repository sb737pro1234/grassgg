package me.sbpro.grassgg.commands.feed;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class FeedConfirmHolder implements InventoryHolder {

    private final Player target;

    public FeedConfirmHolder(Player target) {
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