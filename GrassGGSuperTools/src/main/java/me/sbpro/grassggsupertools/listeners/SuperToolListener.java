package me.sbpro.grassggsupertools.listeners;

import me.sbpro.grassggsupertools.GrassGGSuperTools;
import me.sbpro.grassggsupertools.tools.SuperToolManager;
import me.sbpro.grassggsupertools.tools.SuperToolType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class SuperToolListener implements Listener {

    private final GrassGGSuperTools plugin;
    private final SuperToolManager toolManager;
    private final Set<UUID> processing = new HashSet<>();
    private final NamespacedKey durabilityRemainderKey;

    public SuperToolListener(GrassGGSuperTools plugin) {
        this.plugin = plugin;
        this.toolManager = new SuperToolManager(plugin);
        this.durabilityRemainderKey = new NamespacedKey(plugin, "durability_remainder");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        SuperToolType type = toolManager.getType(tool);

        if (type == null || processing.contains(player.getUniqueId())) {
            return;
        }

        processing.add(player.getUniqueId());
        try {
            switch (type) {
                case PICKAXE_SILK, PICKAXE_FORTUNE, SHOVEL_SILK, SHOVEL_FORTUNE -> {
                    event.setCancelled(true);
                    int broken = breakArea(event.getBlock(), player, tool);
                    damageTool(tool, player, broken);
                }
                case AXE -> {
                    if (Tag.LOGS.isTagged(event.getBlock().getType())) {
                        event.setCancelled(true);
                        int broken = breakTree(event.getBlock(), player, tool);
                        damageTool(tool, player, broken);
                    }
                }
                case HOE -> handleHoe(event, player, tool);
            }
        } finally {
            processing.remove(player.getUniqueId());
        }
    }

    private int breakArea(Block center, Player player, ItemStack tool) {
        int broken = 0;
        Vector face = player.getEyeLocation().getDirection();
        double dx = Math.abs(face.getX());
        double dy = Math.abs(face.getY());
        double dz = Math.abs(face.getZ());

        if (dy >= dx && dy >= dz) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue;
                    if (tryBreakExtra(center.getRelative(x, 0, z), player, tool)) broken++;
                }
            }
        } else if (dx >= dz) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (y == 0 && z == 0) continue;
                    if (tryBreakExtra(center.getRelative(0, y, z), player, tool)) broken++;
                }
            }
        } else {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0) continue;
                    if (tryBreakExtra(center.getRelative(x, y, 0), player, tool)) broken++;
                }
            }
        }

        // The centre block is the normal block the player actually broke.
        if (!center.getType().isAir()) {
            center.breakNaturally(tool);
        }
        return broken + 1;
    }

    private boolean tryBreakExtra(Block block, Player player, ItemStack tool) {
        if (!isBreakable(block)) {
            return false;
        }

        org.bukkit.event.block.BlockBreakEvent extraEvent = new org.bukkit.event.block.BlockBreakEvent(block, player);
        Bukkit.getPluginManager().callEvent(extraEvent);

        if (extraEvent.isCancelled()) {
            return false;
        }

        block.breakNaturally(tool);
        return true;
    }

    private boolean isBreakable(Block block) {
        Material material = block.getType();
        if (material.isAir()) return false;
        if (material == Material.BEDROCK || material == Material.END_PORTAL_FRAME) return false;
        return material.isBlock();
    }

    private int breakTree(Block start, Player player, ItemStack tool) {
        Set<Block> logs = new HashSet<>();
        ArrayDeque<Block> queue = new ArrayDeque<>();
        queue.add(start);

        int maxLogs = 512;
        while (!queue.isEmpty() && logs.size() < maxLogs) {
            Block current = queue.removeFirst();
            if (!logs.add(current)) continue;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        Block next = current.getRelative(x, y, z);
                        if (Tag.LOGS.isTagged(next.getType()) && !logs.contains(next)) {
                            queue.add(next);
                        }
                    }
                }
            }
        }

        int broken = 0;
        for (Block log : logs) {
            if (log.equals(start)) continue;
            if (tryBreakExtra(log, player, tool)) broken++;
        }

        if (!start.getType().isAir()) {
            start.breakNaturally(tool);
        }
        return broken + 1;
    }

    private void handleHoe(BlockBreakEvent event, Player player, ItemStack tool) {
        Block block = event.getBlock();
        Material crop = block.getType();
        Material requiredSeed = getReplantMaterial(crop);

        // Only handle the crops that this Super Hoe is designed to harvest.
        if (requiredSeed == null || !(block.getBlockData() instanceof Ageable ageable)) {
            return;
        }

        // Super Hoe cannot break crops before they are fully grown.
        if (ageable.getAge() < ageable.getMaximumAge()) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        if (!removeOne(player, requiredSeed)) {
            // The crop still breaks normally if there is nothing available to replant it with.
            event.setCancelled(false);
            return;
        }

        block.breakNaturally(tool);

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!player.isOnline() || !block.getType().isAir()) {
                giveBack(player, requiredSeed);
                return;
            }

            if (!isFarmland(block.getRelative(0, -1, 0))) {
                giveBack(player, requiredSeed);
                return;
            }

            block.setType(crop, false);
            if (block.getBlockData() instanceof Ageable replant) {
                replant.setAge(0);
                block.setBlockData(replant, false);
            }
        });

        damageTool(tool, player, 1);
    }

    private boolean isFarmland(Block block) {
        return block.getType() == Material.FARMLAND;
    }

    private Material getReplantMaterial(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            default -> null;
        };
    }

    private boolean removeOne(Player player, Material material) {
        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
            ItemStack item = player.getInventory().getItem(slot);
            if (item != null && item.getType() == material) {
                if (item.getAmount() == 1) {
                    player.getInventory().setItem(slot, null);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
                return true;
            }
        }
        return false;
    }

    private void giveBack(Player player, Material material) {
        ItemStack item = new ItemStack(material, 1);
        player.getInventory().addItem(item).values().forEach(leftover ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));
    }

    private void damageTool(ItemStack tool, Player player, int blocksBroken) {
        if (blocksBroken <= 0 || tool.getType().getMaxDurability() <= 0 || !tool.hasItemMeta()) {
            return;
        }

        Damageable meta = (Damageable) tool.getItemMeta();
        int remainder = meta.getPersistentDataContainer().getOrDefault(
                durabilityRemainderKey, PersistentDataType.INTEGER, 0);

        int total = remainder + blocksBroken;
        int damage = total / 2;
        int newRemainder = total % 2;

        meta.getPersistentDataContainer().set(
                durabilityRemainderKey, PersistentDataType.INTEGER, newRemainder);

        if (damage <= 0) {
            tool.setItemMeta(meta);
            return;
        }

        int newDamage = meta.getDamage() + damage;
        if (newDamage >= tool.getType().getMaxDurability()) {
            player.getInventory().setItemInMainHand(null);
            return;
        }

        meta.setDamage(newDamage);
        tool.setItemMeta(meta);
    }
}
