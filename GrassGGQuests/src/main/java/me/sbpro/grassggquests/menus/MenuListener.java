package me.sbpro.grassggquests.menus;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

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
            if (slot == 11) {
                if (!player.hasPermission("grassgg.coins.shop")) return;
                player.openInventory(me.sbpro.grassggcoins.gui.MenuFactory.createShopMenu(plugin.getCoinsPlugin(), player));
            } else if (slot == 15) {
                plugin.getMenus().openQuestList(player);
            }
            return;
        }

        if (title.equals(ChatColor.stripColor(Messages.QUEST_LIST_TITLE))) {
            int[] rerollSlots = {19, 21, 23, 25};
            for (int i = 0; i < 4; i++) {
                if (slot == rerollSlots[i]) {
                    plugin.getMenus().openRerollConfirmation(player, i);
                    return;
                }
            }
            return;
        }

        if (title.equals(ChatColor.stripColor(Messages.EASIFY_CONFIRM_TITLE))) {
            if (slot == 10) {
                plugin.getMenus().clearPending(player);
                plugin.getMenus().openQuestList(player);
            } else if (slot == 16) {
                Integer index = plugin.getMenus().getPendingReroll(player);
                if (index != null) plugin.getQuestManager().easify(player, index);
                plugin.getMenus().clearPending(player);
                plugin.getMenus().openQuestList(player);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (isOurMenu(title)) event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player && ChatColor.stripColor(event.getView().getTitle()).equals(ChatColor.stripColor(Messages.EASIFY_CONFIRM_TITLE))) {
            plugin.getMenus().clearPending(player);
        }
    }

    private boolean isOurMenu(String title) {
        return title.equals(ChatColor.stripColor(Messages.QUEST_MENU_TITLE))
                || title.equals(ChatColor.stripColor(Messages.QUEST_LIST_TITLE))
                || title.equals(ChatColor.stripColor(Messages.EASIFY_CONFIRM_TITLE));
    }
}
