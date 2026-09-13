package me.sbpro.grassgg.commands.kill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KillListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Kill Confirm")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null) return;

        switch (event.getCurrentItem().getType()) {
            case GREEN_STAINED_GLASS_PANE:
                if (!(event.getInventory().getHolder() instanceof KillConfirmHolder holder)) {
                    return;
                }

                Player target = holder.getTarget();

// Confirm clicked
                target.setHealth(0);
                player.closeInventory();
                break;
            case RED_STAINED_GLASS_PANE:
                player.closeInventory();
                break;
        }
    }
}