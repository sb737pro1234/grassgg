package me.sbpro.grassggquests.menus;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import me.sbpro.grassggquests.data.PlayerData;
import me.sbpro.grassggquests.quests.ActiveQuest;
import me.sbpro.grassggquests.quests.QuestDefinition;
import me.sbpro.grassggquests.shop.ShopItem;
import me.sbpro.grassggquests.shop.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuManager {
    private final GrassGGQuests plugin;
    private final Map<UUID, ShopItem> pendingPurchases = new HashMap<>();

    public MenuManager(GrassGGQuests plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Messages.QUEST_MENU_TITLE);
        inv.setItem(10, button(Material.BOOK, Messages.DAILY_MISSIONS_NAME, Messages.DAILY_MISSIONS_LORE));
        inv.setItem(12, button(Material.WRITABLE_BOOK, Messages.ACTIVE_QUESTS_NAME, Messages.ACTIVE_QUESTS_LORE));
        inv.setItem(14, button(Material.EXPERIENCE_BOTTLE, Messages.QUEST_LEVEL_NAME, Messages.QUEST_LEVEL_LORE));
        inv.setItem(16, button(Material.EMERALD, Messages.QUEST_SHOP_NAME, Messages.QUEST_SHOP_LORE));
        player.openInventory(inv);
    }

    public void openQuestList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Messages.QUEST_LIST_TITLE);
        PlayerData data = plugin.getQuestManager().getData(player.getUniqueId());
        int[] slots = {11, 12, 13, 14, 15};
        for (int i = 0; i < Math.min(5, data.getActiveQuests().size()); i++) {
            ActiveQuest active = data.getActiveQuests().get(i);
            QuestDefinition quest = plugin.getQuestManager().getDefinition(active.getQuestId());
            if (quest == null) continue;
            List<String> lore = new ArrayList<>();
            lore.add(Messages.QUEST_INFO_PREFIX + quest.getDescription());
            lore.add("");
            lore.add(Messages.MAIN_COLOR + "Progress: " + Messages.CUSTOM_COLOR + active.getProgress() + Messages.MAIN_COLOR + " / " + Messages.CUSTOM_COLOR + quest.getRequiredAmount());
            lore.add(Messages.MAIN_COLOR + "Quest XP: " + Messages.CUSTOM_COLOR + quest.getXpReward());
            inv.setItem(slots[i], button(Material.BOOK, Messages.QUEST_TITLE_FORMAT.replace("%quest%", quest.getTitle()), lore));
        }
        player.openInventory(inv);
    }

    public void openLevel(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Messages.QUEST_LEVEL_TITLE);
        PlayerData data = plugin.getQuestManager().getData(player.getUniqueId());
        int required = plugin.getQuestManager().getRequiredXp(data);
        List<String> lore = new ArrayList<>();
        lore.add(Messages.QUEST_LEVEL_LINE.replace("%level%", String.valueOf(data.getLevel())));
        if (required > 0) {
            lore.add(Messages.QUEST_XP_LINE
                    .replace("%xp%", String.valueOf(data.getXp()))
                    .replace("%required%", String.valueOf(required)));
            lore.add(buildProgress(data.getXp(), required));
        } else {
            lore.add(Messages.MAIN_COLOR + "Maximum configured level reached.");
        }
        inv.setItem(12, button(Material.EXPERIENCE_BOTTLE, Messages.QUEST_LEVEL_DISPLAY_NAME, lore));
        inv.setItem(14, button(Material.EMERALD, Messages.QUEST_SHOP_NAME, Messages.QUEST_SHOP_LORE));
        player.openInventory(inv);
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Messages.SHOP_TITLE);
        for (ShopItem item : plugin.getShopManager().getItems().values()) {
            if (item.slot() == 22 || item.slot() < 0 || item.slot() >= 27) continue;
            ItemStack display = item.displayItem().clone();
            ItemMeta meta = display.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(item.displayName());
                List<String> lore = new ArrayList<>();
                if (meta.getLore() != null) lore.addAll(meta.getLore());
                lore.add("");
                lore.add(Messages.MAIN_COLOR + "Cost: " + Messages.CUSTOM_COLOR + item.cost() + " Quest Points");
                meta.setLore(lore);
                display.setItemMeta(meta);
            }
            inv.setItem(item.slot(), display);
        }
        PlayerData data = plugin.getQuestManager().getData(player.getUniqueId());
        inv.setItem(22, button(Material.SUNFLOWER, Messages.QUEST_POINTS_DISPLAY_NAME,
                replace(Messages.QUEST_POINTS_LORE, "%points%", String.valueOf(data.getQuestPoints()))));
        player.openInventory(inv);
    }

    public void openConfirmation(Player player, ShopItem item) {
        pendingPurchases.put(player.getUniqueId(), item);
        Inventory inv = Bukkit.createInventory(null, 27, Messages.CONFIRM_TITLE);
        inv.setItem(10, button(Material.RED_STAINED_GLASS_PANE, Messages.CANCEL_NAME, Messages.CANCEL_LORE));
        inv.setItem(13, item.displayItem().clone());
        inv.setItem(16, button(Material.GREEN_STAINED_GLASS_PANE, Messages.CONFIRM_NAME, Messages.CONFIRM_LORE));
        player.openInventory(inv);
    }

    public ShopItem getPending(Player player) { return pendingPurchases.get(player.getUniqueId()); }
    public void clearPending(Player player) { pendingPurchases.remove(player.getUniqueId()); }

    private String buildProgress(int current, int required) {
        double percent = required <= 0 ? 1 : Math.min(1D, (double) current / required);
        int filled = (int) Math.round(percent * Messages.PROGRESS_LENGTH);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < Messages.PROGRESS_LENGTH; i++) bar.append(i < filled ? Messages.PROGRESS_FILLED : Messages.PROGRESS_EMPTY);
        return bar.toString();
    }

    private ItemStack button(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private List<String> replace(List<String> source, String key, String value) {
        List<String> result = new ArrayList<>();
        for (String line : source) result.add(line.replace(key, value));
        return result;
    }
}
