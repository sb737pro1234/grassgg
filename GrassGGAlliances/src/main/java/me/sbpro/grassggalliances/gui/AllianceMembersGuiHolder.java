package me.sbpro.grassggalliances.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class AllianceMembersGuiHolder implements InventoryHolder {
    private final String allianceName;

    public AllianceMembersGuiHolder(String allianceName) {
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
