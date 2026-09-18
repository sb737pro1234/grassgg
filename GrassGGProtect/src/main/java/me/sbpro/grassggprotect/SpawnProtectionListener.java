package me.sbpro.grassggprotect;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sbpro.grassggprotect.region.RegionFlag;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnProtectionListener implements Listener {

    private static final long WARNING_COOLDOWN = 3000L;

    private final GrassGGProtect plugin;
    private final Map<UUID, Long> warningCooldowns = new HashMap<>();

    public SpawnProtectionListener(GrassGGProtect plugin) {
        this.plugin = plugin;
    }

    /*
     * ==========================================
     * HELPERS
     * ==========================================
     */

    private boolean isAdmin(Player player) {
        return player.hasPermission("grassgg.builder");
    }

    private void sendWarning(Player player, String message) {

        long now = System.currentTimeMillis();
        Long lastWarning = warningCooldowns.get(player.getUniqueId());

        if (lastWarning != null && now - lastWarning < WARNING_COOLDOWN) {
            return;
        }

        warningCooldowns.put(player.getUniqueId(), now);
        player.sendMessage(message);
    }

    private boolean isConsumable(ItemStack item) {
        return item != null
                && !item.isEmpty()
                && item.hasData(DataComponentTypes.CONSUMABLE);
    }

    private boolean isSpawnEgg(ItemStack item) {
        return item != null
                && !item.isEmpty()
                && item.getType().name().endsWith("_SPAWN_EGG");
    }

    private boolean isDoor(BlockData blockData) {
        return blockData instanceof Door;
    }

    private boolean isTrapdoor(BlockData blockData) {
        return blockData instanceof TrapDoor;
    }

    /*
     * ==========================================
     * BLOCK BREAK
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                event.getBlock().getLocation(),
                RegionFlag.BLOCK_BREAK
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.BLOCK_BREAK);
        }
    }

    /*
     * ==========================================
     * BLOCK PLACE
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                event.getBlock().getLocation(),
                RegionFlag.BLOCK_PLACE
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.BLOCK_PLACE);
        }
    }

    /*
     * ==========================================
     * BLOCK / ITEM INTERACTION
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        Action action = event.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();

        /*
         * Wind charges have their own flag and are deliberately
         * not controlled by item_interact.
         */
        if (item != null && item.getType() == Material.WIND_CHARGE) {

            if (!plugin.getRegionManager().isAllowed(
                    player.getLocation(),
                    RegionFlag.WINDCHARGES
            )) {
                event.setCancelled(true);
                sendWarning(player, Messages.WINDCHARGES);
            }

            return;
        }

        /*
         * Doors and trapdoors have their own flag and are deliberately
         * not controlled by block_interact.
         */
        /*
         * Doors have their own flag and are deliberately
         * not controlled by block_interact.
         */
        if (event.getClickedBlock() != null
                && isDoor(event.getClickedBlock().getBlockData())) {

            if (!plugin.getRegionManager().isAllowed(
                    event.getClickedBlock().getLocation(),
                    RegionFlag.DOORS
            )) {
                event.setCancelled(true);
                sendWarning(player, Messages.DOORS);
            }

            return;
        }

        /*
         * Trapdoors have their own flag and are deliberately
         * not controlled by block_interact.
         */
        if (event.getClickedBlock() != null
                && isTrapdoor(event.getClickedBlock().getBlockData())) {

            if (!plugin.getRegionManager().isAllowed(
                    event.getClickedBlock().getLocation(),
                    RegionFlag.TRAPDOORS
            )) {
                event.setCancelled(true);
                sendWarning(player, Messages.TRAPDOORS);
            }

            return;
        }

        /*
         * Normal block interaction.
         */
        if (event.getClickedBlock() != null) {

            if (!plugin.getRegionManager().isAllowed(
                    event.getClickedBlock().getLocation(),
                    RegionFlag.BLOCK_INTERACT
            )) {
                event.setCancelled(true);

                // Keep empty-hand block interaction silent, matching the old behaviour.
                if (item != null && !item.isEmpty()) {
                    sendWarning(player, Messages.BLOCK_INTERACT);
                }

                return;
            }
        }

        /*
         * Normal item interaction.
         *
         * Eating, wind charges and spawn eggs are excluded because
         * they each have their own independent protection flag.
         */
        if (item == null
                || item.isEmpty()
                || isConsumable(item)
                || isSpawnEgg(item)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.ITEM_INTERACT
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.ITEM_INTERACT);
        }
    }

    /*
     * ==========================================
     * EATING / CONSUMING
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerConsume(PlayerItemConsumeEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.EAT
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.EAT);
        }
    }

    /*
     * ==========================================
     * ENTITY INTERACTION
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.ENTITY_INTERACT
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.ENTITY_INTERACT);
        }
    }

    /*
     * ==========================================
     * PVP
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = null;

        if (event.getDamager() instanceof Player player) {
            attacker = player;
        } else if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player player) {
            attacker = player;
        }

        if (attacker == null || isAdmin(attacker)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                victim.getLocation(),
                RegionFlag.PVP
        )) {
            event.setCancelled(true);
            sendWarning(attacker, Messages.PVP);
        }
    }

    /*
     * ==========================================
     * ENVIRONMENTAL DAMAGE
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (isAdmin(player)) {
            return;
        }

        RegionFlag flag = getDamageFlag(event.getCause());

        if (flag == null) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(player.getLocation(), flag)) {
            event.setCancelled(true);
        }
    }

    private RegionFlag getDamageFlag(EntityDamageEvent.DamageCause cause) {

        return switch (cause) {
            case FALL -> RegionFlag.FALL_DAMAGE;
            case FIRE, FIRE_TICK -> RegionFlag.FIRE_DAMAGE;
            case LAVA -> RegionFlag.LAVA_DAMAGE;
            case DROWNING -> RegionFlag.DROWNING;
            case SUFFOCATION -> RegionFlag.SUFFOCATION;
            case VOID -> RegionFlag.VOID_DAMAGE;
            case PROJECTILE -> RegionFlag.PROJECTILE_DAMAGE;
            default -> null;
        };
    }

    /*
     * ==========================================
     * HUNGER
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.HUNGER
        )) {
            event.setCancelled(true);
        }
    }

    /*
     * ==========================================
     * ITEM DROP / PICKUP
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.ITEM_DROP
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.ITEM_DROP);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (isAdmin(player)) {
            return;
        }

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.ITEM_PICKUP
        )) {
            event.setCancelled(true);
            sendWarning(player, Messages.ITEM_PICKUP);
        }
    }

    /*
     * ==========================================
     * EXPLOSIONS
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {

        if (!plugin.getRegionManager().isAllowed(
                event.getLocation(),
                RegionFlag.EXPLOSIONS
        )) {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {

        if (!plugin.getRegionManager().isAllowed(
                event.getBlock().getLocation(),
                RegionFlag.EXPLOSIONS
        )) {
            event.blockList().clear();
        }
    }

    /*
     * ==========================================
     * BLOCK BURN
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {

        if (!plugin.getRegionManager().isAllowed(
                event.getBlock().getLocation(),
                RegionFlag.BLOCK_BURN
        )) {
            event.setCancelled(true);
        }
    }

    /*
     * ==========================================
     * WEATHER
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {

        if (!plugin.getRegionManager().isAllowed(
                event.getWorld().getSpawnLocation(),
                RegionFlag.WEATHER
        )) {
            event.setCancelled(true);
        }
    }

    /*
     * ==========================================
     * MOB SPAWNING
     * ==========================================
     *
     * SPAWNER_EGG = spawn egg
     * COMMAND     = /summon and command-based creature spawning
     * Everything else is treated as natural/world-driven spawning.
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        RegionFlag flag = switch (event.getSpawnReason()) {
            case SPAWNER_EGG -> RegionFlag.MOB_SPAWN_EGG;
            case COMMAND -> RegionFlag.MOB_SPAWN_COMMAND;
            default -> RegionFlag.MOB_SPAWN_NATURAL;
        };

        if (!plugin.getRegionManager().isAllowed(event.getLocation(), flag)) {
            event.setCancelled(true);
        }
    }
}
