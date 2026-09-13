package me.sbpro.grassggmissions.listeners;

import me.sbpro.grassggmissions.GrassGGMissions;
import me.sbpro.grassggmissions.managers.PlayerMissionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.sbpro.grassggmissions.managers.ResetManager;


public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        PlayerMissionManager manager = GrassGGMissions.getInstance().getPlayerMissionManager();

        manager.loadPlayer(player.getUniqueId());

        ResetManager.checkReset(player);

    }
}

