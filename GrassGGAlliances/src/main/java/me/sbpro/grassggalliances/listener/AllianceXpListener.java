/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.data.Ageable
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.player.PlayerFishEvent
 *  org.bukkit.event.player.PlayerFishEvent$State
 */
package me.sbpro.grassggalliances.listener;

import me.sbpro.grassggalliances.service.AllianceXpService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

public final class AllianceXpListener
implements Listener {
    private final AllianceXpService xpService;

    public AllianceXpListener(AllianceXpService xpService) {
        this.xpService = xpService;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        this.xpService.getBlockEntry(block.getType()).filter(entry -> this.isEligible(block)).ifPresent(entry -> this.xpService.awardXp(event.getPlayer(), entry.xp()));
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        Entity entity = event.getCaught();
        if (!(entity instanceof Item)) {
            return;
        }
        Item item = (Item)entity;
        this.xpService.getFishingEntry(item.getItemStack().getType()).ifPresent(entry -> this.xpService.awardXp(event.getPlayer(), entry.xp()));
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        this.xpService.getSlayingEntry(event.getEntityType()).ifPresent(entry -> this.xpService.awardXp(event.getEntity().getKiller(), entry.xp()));
    }

    private boolean isEligible(Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Ageable) {
            Ageable ageable = (Ageable)blockData;
            return ageable.getAge() >= ageable.getMaximumAge();
        }
        Material type = block.getType();
        return type == Material.SUGAR_CANE || type == Material.CACTUS || type == Material.BAMBOO || type == Material.KELP || type == Material.MELON || type == Material.PUMPKIN || type == Material.ROOTED_DIRT || type.name().endsWith("_LOG") || type.name().endsWith("_ORE") || type == Material.ANCIENT_DEBRIS || type == Material.RED_SAND || type == Material.CLAY || type == Material.SNOW_BLOCK || type == Material.SOUL_SAND || type == Material.SOUL_SOIL || type == Material.MYCELIUM;
    }
}

