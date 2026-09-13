package me.sbpro.fastplace.listeners;

import java.util.Arrays;
import me.sbpro.fastplace.FastPlace;
import me.sbpro.fastplace.manager.FastPlaceManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FastPlaceListener implements Listener {
    private final FastPlaceManager manager;
    private final FastPlace plugin;

    public FastPlaceListener(FastPlaceManager manager, FastPlace plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() == Material.AIR) {
            return;
        }

        if (!this.manager.isEnabled(player.getUniqueId())) {
            return;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem == null || handItem.getType().isEmpty()) {
            return;
        }

        Material material = handItem.getType();

        if (material == Material.SPAWNER) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &c&l(!) &cYou cannot place spawners with fastplace!"));
            return;
        }

        Material placementMaterial = this.getPlacementMaterial(material);
        boolean isCrop = placementMaterial != null;

        if (!(isCrop || (material.isBlock() && material.isItem()))) {
            return;
        }

        if (!isCrop) {
            placementMaterial = material;
        }

        int fpAmount = this.manager.getAmount(player.getUniqueId());
        if (fpAmount <= 0) {
            return;
        }

        ItemStack[] contents = player.getInventory().getContents();
        int totalAvailable = (int) Arrays.stream(contents)
                .filter(i -> i != null && i.getType() == material)
                .mapToLong(ItemStack::getAmount)
                .sum();

        if (totalAvailable == 0) {
            return;
        }

        int toPlace = Math.min(fpAmount, totalAvailable);
        BlockFace blockFace = event.getBlockFace();

        if (this.plugin.isDebugMode()) {
            this.plugin.getLogger().info("Starting placement - Item: " + material.name() + ", Block: " + placementMaterial.name() + ", To Place: " + toPlace + ", Total Available: " + totalAvailable);
            this.plugin.getLogger().info("Is Crop: " + isCrop + ", Is Sapling: " + this.isSapling(material));
        }

        event.setCancelled(true);

        int placed = 0;
        Block currentBlock = clickedBlock.getRelative(blockFace);
        BlockFace placementDirection = isCrop ? this.getCropPlacementDirection(event, player) : blockFace;
        int distanceTraveled = 0;
        final int maxDistance = 256;

        if (this.plugin.isDebugMode()) {
            this.plugin.getLogger().info("Placement direction: " + placementDirection.name()
                    + (isCrop ? ", Clicked position: " + event.getClickedPosition() : ""));
        }

        while (placed < toPlace && distanceTraveled < maxDistance) {
            if (this.plugin.isDebugMode()) {
                this.plugin.getLogger().info("Placement loop - Placed: " + placed + ", Distance: " + distanceTraveled
                        + ", Block: " + currentBlock.getType().name() + " @ " + currentBlock.getX() + "," + currentBlock.getY() + "," + currentBlock.getZ());
            }

            if (currentBlock.getY() >= currentBlock.getWorld().getMaxHeight() || currentBlock.getY() < currentBlock.getWorld().getMinHeight()) {
                if (this.plugin.isDebugMode()) {
                    this.plugin.getLogger().info("Stopped: World height boundary");
                }
                break;
            }

            WorldBorder border = player.getWorldBorder();
            if (border != null && !border.isInside(currentBlock.getLocation().add(0.5, 0.5, 0.5))) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&A&7&F&3&D&0&lFASTPLACE &8» &c&l(!) &cFastplace stopped at your island border."));
                break;
            }

            if (!currentBlock.getType().isEmpty() && !currentBlock.isReplaceable()) {
                if (this.plugin.isDebugMode()) {
                    this.plugin.getLogger().info("Stopped: Block not replaceable - " + currentBlock.getType().name());
                }
                break;
            }

            if (this.isSapling(material)) {
                Block below = currentBlock.getRelative(0, -1, 0);
                if (below.getType() != Material.DIRT && below.getType() != Material.GRASS_BLOCK
                        && below.getType() != Material.PODZOL && below.getType() != Material.COARSE_DIRT) {
                    if (this.plugin.isDebugMode()) {
                        this.plugin.getLogger().info("Stopped: Sapling - Invalid ground below: " + below.getType().name());
                    }
                    break;
                }
            } else if (isCrop) {
                Block below = currentBlock.getRelative(0, -1, 0);
                if (this.plugin.isDebugMode()) {
                    this.plugin.getLogger().info("Checking crop - Below block type: " + below.getType().name() + ", Block data: " + below.getBlockData().getClass().getSimpleName());
                }
                if (!(below.getBlockData() instanceof Farmland)) {
                    if (this.plugin.isDebugMode()) {
                        this.plugin.getLogger().info("Stopped: Crop - Not on farmland, block below is: " + below.getType().name());
                    }
                    break;
                }
            }

            currentBlock.setType(placementMaterial);
            placed++;

            if (this.plugin.isDebugMode()) {
                this.plugin.getLogger().info("Placed block " + placed + " at " + currentBlock.getX() + "," + currentBlock.getY() + "," + currentBlock.getZ());
            }

            currentBlock = currentBlock.getRelative(placementDirection);
            distanceTraveled++;
        }

        if (this.plugin.isDebugMode()) {
            this.plugin.getLogger().info("Placement finished - Total placed: " + placed);
        }

        if (placed > 0) {
            this.removeItemsFromInventory(player, material, placed);
        }
    }

    private boolean isSapling(Material material) {
        return material.name().endsWith("_SAPLING");
    }

    private Material getPlacementMaterial(Material material) {
        return switch (material) {
            case WHEAT_SEEDS -> Material.WHEAT;
            case BEETROOT_SEEDS -> Material.BEETROOTS;
            case CARROT -> Material.CARROTS;
            case POTATO -> Material.POTATOES;
            case MELON_SEEDS -> Material.MELON_STEM;
            case PUMPKIN_SEEDS -> Material.PUMPKIN_STEM;
            default -> null;
        };
    }

    private BlockFace getCropPlacementDirection(PlayerInteractEvent event, Player player) {
        Vector clickedPosition = event.getClickedPosition();
        if (clickedPosition == null) {
            return player.getFacing();
        }

        double offsetX = clickedPosition.getX() - 0.5;
        double offsetZ = clickedPosition.getZ() - 0.5;

        if (Math.abs(offsetX) < 0.1 && Math.abs(offsetZ) < 0.1) {
            return player.getFacing();
        }

        if (Math.abs(offsetX) > Math.abs(offsetZ)) {
            return offsetX > 0.0 ? BlockFace.EAST : BlockFace.WEST;
        }
        return offsetZ > 0.0 ? BlockFace.SOUTH : BlockFace.NORTH;
    }

    private void removeItemsFromInventory(Player player, Material material, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = amount;
        int heldSlot = player.getInventory().getHeldItemSlot();

        for (int i = 9; i < 36 && remaining > 0; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() != material) continue;

            int stackAmount = stack.getAmount();
            if (stackAmount <= remaining) {
                player.getInventory().setItem(i, null);
                remaining -= stackAmount;
            } else {
                stack.setAmount(stackAmount - remaining);
                remaining = 0;
            }
        }

        for (int i = 0; i < 9 && remaining > 0; i++) {
            if (i == heldSlot) continue;
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() != material) continue;

            int stackAmount = stack.getAmount();
            if (stackAmount <= remaining) {
                player.getInventory().setItem(i, null);
                remaining -= stackAmount;
            } else {
                stack.setAmount(stackAmount - remaining);
                remaining = 0;
            }
        }

        if (remaining > 0) {
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem != null && handItem.getType() == material) {
                int stackAmount = handItem.getAmount();
                if (stackAmount <= remaining) {
                    player.getInventory().setItemInMainHand(null);
                } else {
                    handItem.setAmount(stackAmount - remaining);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.manager.disable(event.getPlayer().getUniqueId());
    }
}
