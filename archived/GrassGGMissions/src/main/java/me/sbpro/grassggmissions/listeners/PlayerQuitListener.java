package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.managers.BossBarManager;
import me.sbpro.grassggmissions.managers.PlayerMissionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        PlayerMissionManager manager = GrassGGMissions.getInstance().getPlayerMissionManager();

        manager.removePlayer(event.getPlayer().getUniqueId());
        manager.savePlayer(event.getPlayer().getUniqueId());

        BossBarManager.remove(event.getPlayer());

    }

}