package me.sbpro.grassggstaff.sus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SusReasonMenuHolder
        implements InventoryHolder {

    private final SusPlayer player;
    private final Inventory inventory;

    public SusReasonMenuHolder(
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