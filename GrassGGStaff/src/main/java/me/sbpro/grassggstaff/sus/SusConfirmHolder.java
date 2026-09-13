package me.sbpro.grassggstaff.sus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SusConfirmHolder
        implements InventoryHolder {

    private final SusPlayer player;
    private final SusNote note;
    private final Inventory inventory;

    public SusConfirmHolder(
            SusPlayer player,
            SusNote note
    ) {
        this.player = player;
        this.note = note;
        this.inventory = null;
    }

    public SusPlayer player() {
        return player;
    }

    public SusNote note() {
        return note;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}