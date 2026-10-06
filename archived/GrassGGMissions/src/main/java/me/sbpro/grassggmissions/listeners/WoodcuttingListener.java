package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.managers.ProgressManager;
import me.sbpro.grassggmissions.utils.MissionWorldUtils;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class WoodcuttingListener implements Listener {

    @EventHandler
    public void onStrip(PlayerInteractEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();

        if (!MissionWorldUtils.isMissionWorld(player.getWorld())) {
            return;
        }

        Block block = event.getClickedBlock();

        if (block == null) {
            return;
        }

        Material material = block.getType();

        if (!Tag.LOGS.isTagged(material)) {
            return;
        }

        Material tool = player.getInventory().getItemInMainHand().getType();

        if (!tool.name().endsWith("_AXE")) {
            return;
        }

        /*
         * The PlayerInteractEvent fires BEFORE the log is stripped.
         * We simply count the interaction on a valid log with an axe.
         */

        ProgressManager.handleStrip(player, material);

    }

}