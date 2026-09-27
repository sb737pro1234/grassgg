package me.sbpro.grassggalliances.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class AllianceLevelGuiHolder
implements InventoryHolder {
    private final String allianceName;

    public AllianceLevelGuiHolder(String allianceName) {
        this.allianceName = allianceName;
    }

    public String getAllianceName() {
        return this.allianceName;
    }

    public Inventory getInventory() {
        return null;
    }
}

