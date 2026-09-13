/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 */
package me.sbpro.grassggalliances.listener;

import me.sbpro.grassggalliances.GrassGGAlliances;
import me.sbpro.grassggalliances.service.AllianceBuffService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public final class AlliancePlayerListener
implements Listener {
    private final GrassGGAlliances plugin;
    private final AllianceBuffService buffService;

    public AlliancePlayerListener(GrassGGAlliances plugin, AllianceBuffService buffService) {
        this.plugin = plugin;
        this.buffService = buffService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.buffService.refreshPlayer(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, () -> this.buffService.refreshPlayer(event.getPlayer()));
    }
}

