package me.sbpro.grassggspawns;

import me.sbpro.grassggspawns.region.RegionFlag;

import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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

import org.bukkit.event.weather.WeatherChangeEvent;

public class SpawnProtectionListener implements Listener {

    private final GrassGGSpawns plugin;

    public SpawnProtectionListener(GrassGGSpawns plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Long> warningCooldowns = new HashMap<>();

    private static final long WARNING_COOLDOWN = 3000L;
    /*
     * ==========================================
     * HELPER
     * ==========================================
     */

    private boolean isAdmin(Player player) {
        return player.hasPermission("grassgg.builder");
    }

    private void sendWarning(Player player, String message) {

        long now = System.currentTimeMillis();

        Long lastWarning = warningCooldowns.get(player.getUniqueId());

        if (lastWarning != null
                && now - lastWarning < WARNING_COOLDOWN) {
            return;
        }

        warningCooldowns.put(player.getUniqueId(), now);

        player.sendMessage(message);
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
                player.getLocation(),
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
                player.getLocation(),
                RegionFlag.BLOCK_PLACE
        )) {

            event.setCancelled(true);
            sendWarning(player, Messages.BLOCK_PLACE);
        }
    }

    /*
     * ==========================================
     * BLOCK INTERACTION
     * ==========================================
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (isAdmin(player)) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        /*
         * Only deal with right-click block interaction.
         */
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        /*
         * Doors are always usable, even when block interaction
         * is denied in the region.
         */
        if (Tag.DOORS.isTagged(event.getClickedBlock().getType())) {
            return;
        }

        /*
         * Right-clicking a block with an empty hand is silently
         * cancelled.
         */
        if (player.getInventory().getItemInMainHand().isEmpty()) {

            if (!plugin.getRegionManager().isAllowed(
                    player.getLocation(),
                    RegionFlag.BLOCK_INTERACT
            )) {

                event.setCancelled(true);
            }

            return;
        }

        /*
         * Normal block interaction.
         */
        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                RegionFlag.BLOCK_INTERACT
        )) {

            event.setCancelled(true);
            sendWarning(player, Messages.BLOCK_INTERACT);
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
     *
     * Admin attackers can attack anyone anywhere.
     *
     * Normal players are blocked when the region
     * has pvp: deny.
     *
     * The victim's permissions do not matter.
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        Player attacker = null;

        /*
         * Direct melee attack.
         */
        if (event.getDamager() instanceof Player player) {
            attacker = player;
        }

        /*
         * Projectile attack.
         */
        else if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof Player player) {

            attacker = player;
        }

        /*
         * Damage was not caused by a player.
         */
        if (attacker == null) {
            return;
        }

        /*
         * Admins can PvP anywhere.
         */
        if (isAdmin(attacker)) {
            return;
        }

        /*
         * Check the victim's region.
         */
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

        if (!plugin.getRegionManager().isAllowed(
                player.getLocation(),
                flag
        )) {

            event.setCancelled(true);
        }
    }

    private RegionFlag getDamageFlag(EntityDamageEvent.DamageCause cause) {

        return switch (cause) {

            case FALL ->
                    RegionFlag.FALL_DAMAGE;

            case FIRE, FIRE_TICK ->
                    RegionFlag.FIRE_DAMAGE;

            case LAVA ->
                    RegionFlag.LAVA_DAMAGE;

            case DROWNING ->
                    RegionFlag.DROWNING;

            case SUFFOCATION ->
                    RegionFlag.SUFFOCATION;

            case VOID ->
                    RegionFlag.VOID_DAMAGE;

            case PROJECTILE ->
                    RegionFlag.PROJECTILE_DAMAGE;

            default ->
                    null;
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
     * ITEM DROP
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

    /*
     * ==========================================
     * ITEM PICKUP
     * ==========================================
     */

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
     */

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (!plugin.getRegionManager().isAllowed(
                event.getLocation(),
                RegionFlag.MOB_SPAWN
        )) {

            event.setCancelled(true);
        }
    }
}