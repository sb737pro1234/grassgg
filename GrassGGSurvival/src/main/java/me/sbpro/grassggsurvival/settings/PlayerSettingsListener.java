package me.sbpro.grassggsurvival.settings;

import me.sbpro.grassggsurvival.GrassGGSurvival;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerSettingsListener implements Listener {

    private final GrassGGSurvival plugin;

    public PlayerSettingsListener(GrassGGSurvival plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (plugin.getNightVisionManager().isNightVisionEnabled(player.getUniqueId())) {
            plugin.getNightVisionManager().enableNightVision(player);
        }

        if (plugin.getScoreboardManager().isScoreboardEnabled(player.getUniqueId())) {
            plugin.getScoreboardManager().enableScoreboard(player);
        } else {
            plugin.getScoreboardManager().disableScoreboard(player);
        }
        if (plugin.getGlowManager().isGlowEnabled(player.getUniqueId())) {
            plugin.getGlowManager().enableGlow(player);
        } else {
            plugin.getGlowManager().disableGlow(player);
        }
    }



}