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

public final class DeleteGuiHolder
implements InventoryHolder {
    private final String allianceName;

    public DeleteGuiHolder(String allianceName) {
        this.allianceName = allianceName;
    }

    public String getAllianceName() {
        return this.allianceName;
    }

    public Inventory getInventory() {
        return null;
    }
}

