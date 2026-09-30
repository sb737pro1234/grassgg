package me.sbpro.grassggquests.quests;

import me.sbpro.grassggquests.GrassGGQuests;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class QuestListener implements Listener {
    private final QuestManager questManager;

    public QuestListener(QuestManager questManager) {
        this.questManager = questManager;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.isCancelled()) questManager.addProgress(event.getPlayer(), QuestType.BREAK_BLOCK, event.getBlock().getType().name(), 1);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!event.isCancelled()) questManager.addProgress(event.getPlayer(), QuestType.PLACE_BLOCK, event.getBlock().getType().name(), 1);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) questManager.addProgress(killer, QuestType.KILL_ENTITY, event.getEntityType().name(), 1);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || event.isCancelled()) return;
        ItemStack result = event.getRecipe().getResult();
        int amount = result.getAmount();
        if (event.isShiftClick()) amount = result.getMaxStackSize();
        questManager.addProgress(player, QuestType.CRAFT_ITEM, result.getType().name(), amount);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof Item item)) return;
        questManager.addProgress(event.getPlayer(), QuestType.FISH_ITEM, item.getItemStack().getType().name(), item.getItemStack().getAmount());
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        questManager.addProgress(event.getPlayer(), QuestType.EAT_ITEM, event.getItem().getType().name(), 1);
    }
}
