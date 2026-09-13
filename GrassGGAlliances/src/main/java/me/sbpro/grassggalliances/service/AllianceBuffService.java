/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.sbpro.grassggalliances.service;

import java.util.UUID;
import me.sbpro.grassggalliances.data.AllianceStorage;
import me.sbpro.grassggalliances.model.Alliance;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class AllianceBuffService {
    private static final int INFINITE_DURATION = Integer.MAX_VALUE;
    private final JavaPlugin plugin;
    private final AllianceStorage allianceStorage;

    public AllianceBuffService(JavaPlugin plugin, AllianceStorage allianceStorage) {
        this.plugin = plugin;
        this.allianceStorage = allianceStorage;
    }

    public void refreshAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.refreshPlayer(player);
        }
    }

    public void refreshPlayer(Player player) {
        this.clearPlayer(player);
        this.allianceStorage.getAllianceByPlayer(player.getUniqueId()).ifPresent(alliance -> this.applyLevelEffects(player, alliance.getLevel()));
    }

    public void refreshAlliance(Alliance alliance) {
        for (UUID memberId : alliance.getMembers()) {
            Player player = Bukkit.getPlayer((UUID)memberId);
            if (player == null) continue;
            this.refreshPlayer(player);
        }
    }

    public void clearAlliance(Alliance alliance) {
        for (UUID memberId : alliance.getMembers()) {
            Player player = Bukkit.getPlayer((UUID)memberId);
            if (player == null) continue;
            this.clearPlayer(player);
        }
    }

    public void clearPlayer(Player player) {
        player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }

    private void applyLevelEffects(Player player, int level) {
        this.addEffect(player, PotionEffectType.HEALTH_BOOST, level >= 10 ? 1 : 0);
        if (level >= 3) {
            this.addEffect(player, PotionEffectType.SPEED, level >= 10 ? 1 : 0);
        }
        if (level >= 4) {
            this.addEffect(player, PotionEffectType.JUMP_BOOST, 0);
        }
        if (level >= 5) {
            this.addEffect(player, PotionEffectType.STRENGTH, level >= 10 ? 1 : 0);
        }
    }

    private void addEffect(Player player, PotionEffectType type, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false, true));
    }
}

