package me.sbpro.grassggcoins.gui;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.shop.ShopManager.ShopItem;

import java.util.HashMap;
import java.util.Map;

public final class ShopSlotMap {

    private ShopSlotMap() {
    }

    public static Map<Integer, String> create(GrassGGCoins plugin) {
        Map<Integer, String> slots = new HashMap<>();
        int slot = 0;

        for (ShopItem item : plugin.getShopManager().getItems()) {

            // Slot 49 is reserved for the player's coin balance.
            if (slot == 49) {
                slot++;
            }

            // Shop is full.
            if (slot >= 54) {
                break;
            }

            slots.put(slot, item.identifier());
            slot++;
        }

        return slots;
    }
}