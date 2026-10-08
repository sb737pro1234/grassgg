package me.sbpro.grassggquests.menus;

import me.sbpro.grassggquests.GrassGGQuests;
import me.sbpro.grassggquests.Messages;
import me.sbpro.grassggquests.data.PlayerData;
import me.sbpro.grassggquests.quests.ActiveQuest;
import me.sbpro.grassggquests.quests.QuestDefinition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuManager {
    private final GrassGGQuests plugin;
    private final Map<UUID, Integer> pendingRerolls = new HashMap<>();

    public MenuManager(GrassGGQuests plugin) { this.plugin = plugin; }

    public void openMain(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Messages.QUEST_MENU_TITLE);
        inv.setItem(11, button(Material.EMERALD, Messages.COIN_SHOP_NAME, Messages.COIN_SHOP_LORE));
        inv.setItem(15, button(Material.WRITABLE_BOOK, Messages.ACTIVE_QUESTS_NAME, Messages.ACTIVE_QUESTS_LORE));
        player.openInventory(inv);
    }

    public void openQuestList(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, Messages.QUEST_LIST_TITLE);
        PlayerData data = plugin.getQuestManager().getData(player.getUniqueId());
        int[] questSlots = {10, 12, 14, 16};
        int[] rerollSlots = {19, 21, 23, 25};

        for (int i = 0; i < Math.min(4, data.getActiveQuests().size()); i++) {
            ActiveQuest active = data.getActiveQuests().get(i);
            QuestDefinition quest = plugin.getQuestManager().getDefinition(active.getQuestId());
            if (quest == null) continue;

            List<String> lore = new ArrayList<>();
            lore.add((active.isEasy() ? Messages.EASY_LABEL : Messages.MEDIUM_LABEL));
            lore.add(Messages.PROGRESS_LINE.replace("%progress%", String.valueOf(active.getProgress()))
                    .replace("%required%", String.valueOf(quest.getRequiredAmount())));
            lore.add(Messages.REWARD_LINE.replace("%reward%", active.isEasy() ? "5" : "10"));

            inv.setItem(questSlots[i], button(Material.BOOK,
                    Messages.QUEST_TITLE_FORMAT.replace("%quest%", quest.getTitle()), lore));
            if (!active.isEasy()) inv.setItem(rerollSlots[i], button(Material.REDSTONE, Messages.REROLL_BUTTON_NAME, Messages.REROLL_BUTTON_LORE));
        }
        player.openInventory(inv);
    }

    public void openRerollConfirmation(Player player, int index) {
        PlayerData data = plugin.getQuestManager().getData(player.getUniqueId());
        if (index < 0 || index >= data.getActiveQuests().size()) return;
        ActiveQuest active = data.getActiveQuests().get(index);
        if (active.isEasy()) return;
        QuestDefinition quest = plugin.getQuestManager().getDefinition(active.getQuestId());
        if (quest == null) return;

        pendingRerolls.put(player.getUniqueId(), index);
        Inventory inv = Bukkit.createInventory(null, 27, Messages.EASIFY_CONFIRM_TITLE);
        inv.setItem(10, button(Material.RED_STAINED_GLASS_PANE, Messages.CANCEL_NAME, Messages.CANCEL_LORE));
        inv.setItem(13, questDisplay(active, quest));
        inv.setItem(16, button(Material.GREEN_STAINED_GLASS_PANE, Messages.CONFIRM_NAME, Messages.CONFIRM_LORE));
        player.openInventory(inv);
    }

    private ItemStack questDisplay(ActiveQuest active, QuestDefinition quest) {
        List<String> lore = new ArrayList<>();
        lore.add(active.isEasy() ? Messages.EASY_LABEL : Messages.MEDIUM_LABEL);
        lore.add(Messages.PROGRESS_LINE.replace("%progress%", String.valueOf(active.getProgress()))
                .replace("%required%", String.valueOf(quest.getRequiredAmount())));
        lore.add(Messages.REWARD_LINE.replace("%reward%", active.isEasy() ? "5" : "10"));
        return button(Material.BOOK, Messages.QUEST_TITLE_FORMAT.replace("%quest%", quest.getTitle()), lore);
    }

    public Integer getPendingReroll(Player player) { return pendingRerolls.get(player.getUniqueId()); }
    public void clearPending(Player player) { pendingRerolls.remove(player.getUniqueId()); }

    private ItemStack button(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
