package me.sbpro.grassggstaff.sus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SusDeleteReasonMenuHolder
        implements InventoryHolder {

    private final SusPlayer player;
    private final Inventory inventory;

    public SusDeleteReasonMenuHolder(
            SusPlayer player
    ) {
        this.player = player;
        this.inventory = null;
    }

    public SusPlayer player() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}