package me.sbpro.grassggsurvival.commands.ranks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RanksListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase(RanksCommand.TITLE)){
            event.setCancelled(true);
        }
    }
}
