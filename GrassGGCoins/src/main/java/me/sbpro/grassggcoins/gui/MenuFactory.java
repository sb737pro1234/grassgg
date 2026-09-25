package me.sbpro.grassggcoins.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.data.TopCoinsEntry;
import me.sbpro.grassggcoins.shop.ShopManager.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MenuFactory {

    private MenuFactory() {
    }

    public static Inventory createCoinsMenu(GrassGGCoins plugin, Player player) {
        CoinMenuHolder holder = new CoinMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 27, Messages.coinsMenuTitle());
        holder.setInventory(inventory);

        inventory.setItem(10, createItem(
                Material.EMERALD,
                Messages.shopButtonName(),
                Messages.shopButtonLore()
        ));

        inventory.setItem(12, createItem(
                Material.GOLD_NUGGET,
                Messages.balanceItemName(plugin.getCoinManager().getCoins(player.getUniqueId())),
                Messages.balanceItemLore()
        ));

        inventory.setItem(14, createItem(
                Material.BOOK,
                Messages.infoItemName(),
                Messages.infoItemLore()
        ));

        inventory.setItem(16, createItem(
                Material.PLAYER_HEAD,
                Messages.topButtonName(),
                Messages.topButtonLore()
        ));

        return inventory;
    }

    public static Inventory createShopMenu(GrassGGCoins plugin, Player player) {
        ShopMenuHolder holder = new ShopMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 54, Messages.shopMenuTitle());
        holder.setInventory(inventory);

        /*
         * Use each item's configured Slot from shop.yml.
         * ShopSlotMap also validates the slots and keeps slot 49 reserved
         * for the player's balance.
         */
        for (Map.Entry<Integer, String> entry
                : new java.util.TreeMap<>(ShopSlotMap.create(plugin)).entrySet()) {

            ShopItem shopItem = plugin.getShopManager().getItem(
                    entry.getValue()
            );

            if (shopItem == null) {
                continue;
            }

            ItemStack display = shopItem.hasDisplayItem()
                    ? prepareShopItem(shopItem)
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

                // Always add a blank line before the GrassGGCoins shop information.
                lore.add("");
                lore.addAll(Messages.shopProductLore(shopItem.cost()));

                meta.setLore(lore);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                display.setItemMeta(meta);
            }

            inventory.setItem(entry.getKey(), display);
        }

        // Slot 49 is the centre of the bottom row and is reserved for balance.
        inventory.setItem(
                49,
                createItem(
                        Material.SUNFLOWER,
                        Messages.shopBalanceItemName(
                                plugin.getCoinManager().getCoins(player.getUniqueId())
                        ),
                        Messages.shopBalanceItemLore(
                                plugin.getCoinManager().getCoins(player.getUniqueId())
                        )
                )
        );

        return inventory;
    }

    public static Inventory createTopCoinsMenu(GrassGGCoins plugin, Player player) {
        TopCoinsMenuHolder holder = new TopCoinsMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, 54, Messages.topCoinsMenuTitle());
        holder.setInventory(inventory);

        List<TopCoinsEntry> topPlayers = plugin.getCoinManager().getTopCoins(45);

        for (int slot = 0; slot < topPlayers.size() && slot < 45; slot++) {
            TopCoinsEntry entry = topPlayers.get(slot);
            int position = slot + 1;

            inventory.setItem(
                    slot,
                    createPlayerBalanceHead(
                            entry.uuid(),
                            entry.playerName(),
                            entry.coins(),
                            position
                    )
            );
        }

        // Centre of the bottom row: the player's own balance and full leaderboard position.
        long playerBalance = plugin.getCoinManager().getCoins(player.getUniqueId());
        int playerPosition = plugin.getCoinManager().getPosition(player.getUniqueId());

        inventory.setItem(
                49,
                createPlayerBalanceHead(
                        player,
                        playerBalance,
                        playerPosition
                )
        );

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

        inventory.setItem(
                10,
                createItem(
                        Material.RED_STAINED_GLASS_PANE,
                        Messages.cancelPurchaseName(),
                        Messages.cancelPurchaseLore()
                )
        );

        inventory.setItem(
                13,
                prepareShopItem(shopItem)
        );

        inventory.setItem(
                16,
                createItem(
                        Material.GREEN_STAINED_GLASS_PANE,
                        Messages.confirmPurchaseName(),
                        Messages.confirmPurchaseLore(shopItem.cost())
                )
        );

        return inventory;
    }

    private static ItemStack createPlayerBalanceHead(
            java.util.UUID uuid,
            String playerName,
            long balance,
            int position
    ) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta baseMeta = head.getItemMeta();

        if (!(baseMeta instanceof SkullMeta meta)) {
            return head;
        }

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

        meta.setDisplayName(
                position > 0
                        ? Messages.topCoinsPlayerName(playerName, position)
                        : Messages.topCoinsPlayerNameUnranked(playerName)
        );

        meta.setLore(Messages.topCoinsPlayerLore(balance));

        head.setItemMeta(meta);

        // Hide the player-head profile tooltip entry while keeping the skin.
        head.setData(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                        .addHiddenComponents(DataComponentTypes.PROFILE)
                        .build()
        );

        return head;
    }

    private static ItemStack createPlayerBalanceHead(
            Player player,
            long balance,
            int position
    ) {
        return createPlayerBalanceHead(
                player.getUniqueId(),
                player.getName(),
                balance,
                position
        );
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

    private static ItemStack prepareShopItem(ShopItem shopItem) {
        ItemStack display = shopItem.displayItem().clone();

        ItemMeta meta = display.getItemMeta();

        if (meta == null) {
            return display;
        }

        meta.setDisplayName(
                ChatColor.translateAlternateColorCodes(
                        '&',
                        shopItem.displayName()
                )
        );

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        display.setItemMeta(meta);

        return display;
    }
}
