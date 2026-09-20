package me.sbpro.grassggcoins.listener;

import me.sbpro.grassggcoins.GrassGGCoins;
import me.sbpro.grassggcoins.Messages;
import me.sbpro.grassggcoins.gui.CoinMenuHolder;
import me.sbpro.grassggcoins.gui.ConfirmationMenuHolder;
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

        // =============================================================
        // /coins menu
        // =============================================================

        if (event.getView().getTopInventory().getHolder() instanceof CoinMenuHolder) {
            event.setCancelled(true);

            if (event.getRawSlot() == 11) {
                player.openInventory(MenuFactory.createShopMenu(plugin));
            }

            return;
        }

        // =============================================================
        // Coin shop
        // =============================================================

        if (event.getView().getTopInventory().getHolder() instanceof ShopMenuHolder) {
            event.setCancelled(true);

            if (event.getRawSlot() < 0
                    || event.getRawSlot() >= event.getView().getTopInventory().getSize()) {
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

            /*
             * Player does not have enough coins.
             * Keep the player in the shop and show the error + sound.
             */
            if (balance < item.cost()) {
                player.sendMessage(
                        Messages.insufficientCoins(balance, item.cost())
                );

                player.playSound(
                        player.getLocation(),
                        Messages.INSUFFICIENT_COINS_SOUND,
                        Messages.INSUFFICIENT_COINS_SOUND_VOLUME,
                        Messages.INSUFFICIENT_COINS_SOUND_PITCH
                );

                return;
            }

            /*
             * Player can afford it, so show the confirmation menu.
             */
            player.openInventory(
                    MenuFactory.createConfirmationMenu(plugin, item)
            );

            return;
        }

        // =============================================================
        // Purchase confirmation menu
        // =============================================================

        if (event.getView().getTopInventory().getHolder() instanceof ConfirmationMenuHolder holder) {
            event.setCancelled(true);

            int slot = event.getRawSlot();

            // Cancel
            if (slot == 10) {
                player.openInventory(MenuFactory.createShopMenu(plugin));
                return;
            }

            // Only continue when Confirm is clicked.
            if (slot != 16) {
                return;
            }

            ShopItem item = plugin.getShopManager().getItem(holder.getIdentifier());

            /*
             * The shop item may have been removed/changed while the
             * confirmation menu was open.
             */
            if (item == null || !item.hasDisplayItem()) {
                player.openInventory(MenuFactory.createShopMenu(plugin));
                return;
            }

            /*
             * Check the balance again here.
             *
             * This is important because the player could have had
             * enough coins when opening the confirmation menu but
             * then had their balance changed before clicking Confirm.
             */
            long balance = plugin.getCoinManager().getCoins(player.getUniqueId());

            if (balance < item.cost()) {
                player.sendMessage(
                        Messages.insufficientCoins(balance, item.cost())
                );

                player.playSound(
                        player.getLocation(),
                        Messages.INSUFFICIENT_COINS_SOUND,
                        Messages.INSUFFICIENT_COINS_SOUND_VOLUME,
                        Messages.INSUFFICIENT_COINS_SOUND_PITCH
                );

                return;
            }

            /*
             * Take the coins only when the player actually confirms.
             */
            if (!plugin.getCoinManager().takeCoins(
                    player.getUniqueId(),
                    item.cost()
            )) {
                return;
            }

            /*
             * Execute the configured shop command as console.
             */
            if (!runPurchaseCommand(player, item)) {

                /*
                 * If the command failed, refund the coins.
                 */
                plugin.getCoinManager().addCoins(
                        player.getUniqueId(),
                        item.cost()
                );

                player.sendMessage(
                        Messages.purchaseCommandFailed()
                );

                return;
            }

            player.sendMessage(
                    Messages.purchased(
                            item.identifier(),
                            item.cost()
                    )
            );

            /*
             * Return to the shop after a successful purchase.
             */
            player.openInventory(
                    MenuFactory.createShopMenu(plugin)
            );

            return;
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (event.getView().getTopInventory().getHolder() instanceof CoinMenuHolder
                || event.getView().getTopInventory().getHolder() instanceof ShopMenuHolder
                || event.getView().getTopInventory().getHolder() instanceof ConfirmationMenuHolder) {

            event.setCancelled(true);
        }
    }

    private boolean runPurchaseCommand(Player player, ShopItem item) {

        String command = item.command()
                .replace("%player%", player.getName());

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        return Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command
        );
    }
}