package me.sbpro.grassggquests.menus;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import me.sbpro.grassggquests.data.PlayerData;
import me.sbpro.grassggquests.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuListener implements Listener {
    private final GrassGGQuests plugin;

    public MenuListener(GrassGGQuests plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!isOurMenu(title)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();
        if (title.equals(ChatColor.stripColor(Messages.QUEST_MENU_TITLE))) {
            if (slot == 10) {
                player.closeInventory();
                player.performCommand("dailymissions");
            } else if (slot == 12) plugin.getMenus().openQuestList(player);
            else if (slot == 14) plugin.getMenus().openLevel(player);
            else if (slot == 16) plugin.getMenus().openShop(player);
        } else if (title.equals(ChatColor.stripColor(Messages.QUEST_LEVEL_TITLE))) {
            if (slot == 14) plugin.getMenus().openShop(player);
        } else if (title.equals(ChatColor.stripColor(Messages.SHOP_TITLE))) {
            if (slot == 22) return;
            ShopItem item = findShopItemBySlot(slot);
            if (item != null) plugin.getMenus().openConfirmation(player, item);
        } else if (title.equals(ChatColor.stripColor(Messages.CONFIRM_TITLE))) {
            if (slot == 10) {
                plugin.getMenus().clearPending(player);
                plugin.getMenus().openShop(player);
            } else if (slot == 16) confirm(player);
        }
    }

    private void confirm(Player player) {
        ShopItem item = plugin.getMenus().getPending(player);
        if (item == null) { plugin.getMenus().openShop(player); return; }
        PlayerData data = plugin.getQuestManager().getData(player.getUniqueId());
        if (data.getQuestPoints() < item.cost()) {
            player.sendMessage(Messages.PREFIX + Messages.NOT_ENOUGH_POINTS);
            return;
        }
        data.setQuestPoints(data.getQuestPoints() - item.cost());
        plugin.getDataManager().save(data);
        String command = item.command().replace("%player%", player.getName());
        if (!command.isBlank()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        player.sendMessage(Messages.PREFIX + Messages.PURCHASE_SUCCESS);
        plugin.getMenus().clearPending(player);
        player.closeInventory();
    }

    private ShopItem findShopItemBySlot(int slot) {
        for (ShopItem item : plugin.getShopManager().getItems().values()) if (item.slot() == slot) return item;
        return null;
    }

    private boolean isOurMenu(String title) {
        return title.equals(ChatColor.stripColor(Messages.QUEST_MENU_TITLE))
                || title.equals(ChatColor.stripColor(Messages.QUEST_LIST_TITLE))
                || title.equals(ChatColor.stripColor(Messages.QUEST_LEVEL_TITLE))
                || title.equals(ChatColor.stripColor(Messages.SHOP_TITLE))
                || title.equals(ChatColor.stripColor(Messages.CONFIRM_TITLE));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            String title = ChatColor.stripColor(event.getView().getTitle());
            if (title.equals(ChatColor.stripColor(Messages.CONFIRM_TITLE))) plugin.getMenus().clearPending(player);
        }
    }
}
