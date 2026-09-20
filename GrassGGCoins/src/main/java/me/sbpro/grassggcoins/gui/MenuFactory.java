package me.sbpro.grassggcoins.gui;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.shop.ShopManager.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
                    : createItem(Material.BARRIER, Messages.shopEmptyItemName(), Messages.shopEmptyItemLore());

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

    private static ItemStack createItem(Material material, String name, List<String> lore) {
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
