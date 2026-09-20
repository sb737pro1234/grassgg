package me.sbpro.grassggcoins.listener;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.gui.CoinMenuHolder;
import me.sbpro.grassggcoins.gui.MenuFactory;
import me.sbpro.grassggcoins.gui.ShopMenuHolder;
import me.sbpro.grassggcoins.gui.ShopSlotMap;
import me.sbpro.grassggcoins.shop.ShopManager.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Map;

public final class CoinMenuListener implements Listener {

    private final GrassGGCoins plugin;

    public CoinMenuListener(GrassGGCoins plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getView().getTopInventory().getHolder() instanceof CoinMenuHolder) {
            event.setCancelled(true);

            if (event.getRawSlot() == 11) {
                player.openInventory(MenuFactory.createShopMenu(plugin));
            }
            return;
        }

        if (!(event.getView().getTopInventory().getHolder() instanceof ShopMenuHolder)) {
            return;
        }

        event.setCancelled(true);

        if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
            return;
        }

        Map<Integer, String> slotMap = ShopSlotMap.create(plugin);
        String identifier = slotMap.get(event.getRawSlot());
        if (identifier == null) {
            return;
        }

        ShopItem item = plugin.getShopManager().getItem(identifier);
        if (item == null || !item.hasDisplayItem()) {
            return;
        }

        long balance = plugin.getCoinManager().getCoins(player.getUniqueId());
        if (balance < item.cost()) {
            player.sendMessage(Messages.insufficientCoins(balance, item.cost()));
            player.playSound(player.getLocation(), Messages.INSUFFICIENT_COINS_SOUND,
                    Messages.INSUFFICIENT_COINS_SOUND_VOLUME, Messages.INSUFFICIENT_COINS_SOUND_PITCH);
            return;
        }

        if (!plugin.getCoinManager().takeCoins(player.getUniqueId(), item.cost())) {
            return;
        }

        if (!runPurchaseCommand(player, item)) {
            plugin.getCoinManager().addCoins(player.getUniqueId(), item.cost());
            player.sendMessage(Messages.purchaseCommandFailed());
            return;
        }

        player.sendMessage(Messages.purchased(item.identifier(), item.cost()));
        player.openInventory(MenuFactory.createShopMenu(plugin));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof CoinMenuHolder
                || event.getView().getTopInventory().getHolder() instanceof ShopMenuHolder) {
            event.setCancelled(true);
        }
    }

    private boolean runPurchaseCommand(Player player, ShopItem item) {
        String command = item.command().replace("%player%", player.getName());
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
