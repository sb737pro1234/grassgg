package me.sbpro.grassggalliances.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class AllianceInfoGuiHolder implements InventoryHolder {
    private final String allianceName;

    public AllianceInfoGuiHolder(String allianceName) {
        this.allianceName = allianceName;
    }

    public String getAllianceName() {
        return allianceName;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
