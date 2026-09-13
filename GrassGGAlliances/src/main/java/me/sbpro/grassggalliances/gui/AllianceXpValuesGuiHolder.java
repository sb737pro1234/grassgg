/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package me.sbpro.grassggalliances.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class AllianceXpValuesGuiHolder
implements InventoryHolder {
    private final String allianceName;
    private final int page;

    public AllianceXpValuesGuiHolder(String allianceName, int page) {
        this.allianceName = allianceName;
        this.page = page;
    }

    public String getAllianceName() {
        return this.allianceName;
    }

    public int getPage() {
        return this.page;
    }

    public Inventory getInventory() {
        return null;
    }
}

