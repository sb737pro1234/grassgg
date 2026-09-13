package me.sbpro.grassggstaff.sus;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public final class SusMenuHolder
        implements InventoryHolder {

    private final int page;
    private final List<SusPlayer> players;

    public SusMenuHolder(
            int page,
            List<SusPlayer> players
    ) {
        this.page = page;
        this.players = players;
    }

    public int page() {
        return page;
    }

    public List<SusPlayer> players() {
        return players;
    }

    public SusPlayer playerAt(
            int slot
    ) {

        if (slot < 0 || slot >= 45) {
            return null;
        }

        int index =
                (page * 45) + slot;

        if (index < 0 || index >= players.size()) {
            return null;
        }

        return players.get(index);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}