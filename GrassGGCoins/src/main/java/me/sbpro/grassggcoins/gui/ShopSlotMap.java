package me.sbpro.grassggcoins.gui;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.shop.ShopManager.ShopItem;

import java.util.HashMap;
import java.util.Map;

public final class ShopSlotMap {

    /**
     * The shop uses a 54-slot inventory.
     *
     * Slot 49 is reserved for the player's balance item.
     */
    public static final int SHOP_SIZE = 54;
    public static final int RESERVED_BALANCE_SLOT = 49;

    private ShopSlotMap() {
    }

    public static Map<Integer, String> create(GrassGGCoins plugin) {
        Map<Integer, String> slots = new HashMap<>();

        for (ShopItem item : plugin.getShopManager().getItems()) {
            int configuredSlot = item.slot();

            // No slot configured.
            if (configuredSlot < 0) {
                plugin.getLogger().warning(
                        "Shop item '" + item.identifier()
                                + "' has no valid Slot configured and will not be displayed."
                );
                continue;
            }

            // Slot is outside the 54-slot shop.
            if (configuredSlot >= SHOP_SIZE) {
                plugin.getLogger().warning(
                        "Shop item '" + item.identifier()
                                + "' uses invalid slot " + configuredSlot
                                + ". Valid slots are 0-53."
                );
                continue;
            }

            // Slot 49 is reserved for the player's balance.
            if (configuredSlot == RESERVED_BALANCE_SLOT) {
                plugin.getLogger().warning(
                        "Shop item '" + item.identifier()
                                + "' is trying to use reserved slot "
                                + RESERVED_BALANCE_SLOT
                                + " and will not be displayed."
                );
                continue;
            }

            // Do not allow two shop items to use the same slot.
            if (slots.containsKey(configuredSlot)) {
                plugin.getLogger().warning(
                        "Shop item '" + item.identifier()
                                + "' is trying to use slot "
                                + configuredSlot
                                + ", but that slot is already used by '"
                                + slots.get(configuredSlot)
                                + "'. The item will not be displayed."
                );
                continue;
            }

            slots.put(configuredSlot, item.identifier());
        }

        return slots;
    }
}
