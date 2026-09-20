package me.sbpro.grassggcoins.gui;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.shop.ShopManager.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class MenuFactory {

    private MenuFactory() {
    }

    public static Inventory createCoinsMenu(GrassGGCoins plugin, Player player) {
        CoinMenuHolder holder = new CoinMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 27, Messages.coinsMenuTitle());
        holder.setInventory(inventory);

        inventory.setItem(11, createItem(
                Material.EMERALD,
                Messages.shopButtonName(),
                Messages.shopButtonLore()
        ));

        inventory.setItem(13, createItem(
                Material.GOLD_NUGGET,
                Messages.balanceItemName(plugin.getCoinManager().getCoins(player.getUniqueId())),
                Messages.balanceItemLore()
        ));

        inventory.setItem(15, createItem(
                Material.BOOK,
                Messages.infoItemName(),
                Messages.infoItemLore()
        ));

        return inventory;
    }

    public static Inventory createShopMenu(GrassGGCoins plugin) {
        ShopMenuHolder holder = new ShopMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 54, Messages.shopMenuTitle());
        holder.setInventory(inventory);

        int slot = 0;

        for (ShopItem shopItem : plugin.getShopManager().getItems()) {
            if (slot >= inventory.getSize()) {
                break;
            }

            ItemStack display = shopItem.hasDisplayItem()
                    ? shopItem.displayItem().clone()
                    : createItem(
                    Material.BARRIER,
                    Messages.shopEmptyItemName(),
                    Messages.shopEmptyItemLore()
            );

            ItemMeta meta = display.getItemMeta();

            if (meta != null) {
                List<String> lore = meta.getLore() == null
                        ? new ArrayList<>()
                        : new ArrayList<>(meta.getLore());

                if (!lore.isEmpty()) {
                    lore.add("");
                }

                lore.addAll(Messages.shopProductLore(shopItem.cost()));

                meta.setLore(lore);
                display.setItemMeta(meta);
            }

            inventory.setItem(slot++, display);
        }

        return inventory;
    }

    public static Inventory createConfirmationMenu(GrassGGCoins plugin, ShopItem shopItem) {
        ConfirmationMenuHolder holder =
                new ConfirmationMenuHolder(shopItem.identifier());

        Inventory inventory = Bukkit.createInventory(
                holder,
                27,
                Messages.confirmationMenuTitle()
        );

        holder.setInventory(inventory);

        /*
         * Slot 10 = Cancel
         * Slot 13 = Display Item
         * Slot 16 = Confirm
         */

        inventory.setItem(
                10,
                createItem(
                        Material.RED_WOOL,
                        Messages.cancelPurchaseName(),
                        Messages.cancelPurchaseLore()
                )
        );

        /*
         * The item displayed here is the exact ItemStack stored in shop.yml.
         * Nothing is added to it, so enchantments, lore, names, components,
         * custom model data, etc. are preserved.
         */
        inventory.setItem(
                13,
                shopItem.displayItem().clone()
        );

        inventory.setItem(
                16,
                createItem(
                        Material.LIME_WOOL,
                        Messages.confirmPurchaseName(),
                        Messages.confirmPurchaseLore(shopItem.cost())
                )
        );

        return inventory;
    }

    private static ItemStack createItem(
            Material material,
            String name,
            List<String> lore
    ) {
        ItemStack stack = new ItemStack(material);

        ItemMeta meta = stack.getItemMeta();

        if (meta == null) {
            return stack;
        }

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        stack.setItemMeta(meta);

        return stack;
    }
}