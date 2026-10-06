package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.managers.RerollManager;
import me.sbpro.grassggmissions.menus.DailyMissionsMenu;
import me.sbpro.grassggmissions.missions.PlayerMissionData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals(DailyMissionsMenu.TITLE)) {
            return;
        }
        event.setCancelled(true);


        Player player = (Player) event.getWhoClicked();

        switch (event.getRawSlot()) {

            case 19 -> {
                RerollManager.reroll(player, 1);
                DailyMissionsMenu.open(player);
            }

            case 21 -> {
                RerollManager.reroll(player, 2);
                DailyMissionsMenu.open(player);
            }

            case 23 -> {
                RerollManager.reroll(player, 3);
                DailyMissionsMenu.open(player);
            }

            case 25 -> {
                RerollManager.reroll(player, 4);
                DailyMissionsMenu.open(player);
            }

        }


    }

}